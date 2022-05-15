package me.fizzika.tankirating.v1_migration.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.v1_migration.repository.AccountMongoRepository;
import me.fizzika.tankirating.v1_migration.service.V1MigrationService;
import me.fizzika.tankirating.v1_migration.service.impl.account.AccountMigrationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
@Profile("migration")
@RequiredArgsConstructor
public class AccountMigrationService implements V1MigrationService {

    private final AccountMongoRepository mongoRepository;

    private final AccountMigrationRunner migrationRunner;

    @Override
    public void migrate() {
        List<String> logins = mongoRepository.getAccountLogins();
        log.info("Starting migration for {} accounts", logins.size());

        LocalDateTime start = LocalDateTime.now();

        /*
            Fun story: single-thread migration processes 3 users/minute, so that takes more than 1 HOUR to load all V1 users
            In the same time multi-thread migration processes 283 users/minute, which is more that x9 faster that single-thread approach
            Multi-thread migration loaded all users in less than 1 minute! (46 seconds).
            (All tests are made on Intel i7-9700 (8) @ 4.700GHz)

            One disadvantage: we can't use standard (SERIALIZABLE) transactional with multi-thread migration, thanks to EntityService and
            it's caching policy. We need transaction with dirty read support to handle this issue.
         */
        var migrationTask = CompletableFuture.allOf(logins.stream()
                .map(mongoRepository::findAccountDocumentByLogin)
                .map(migrationRunner::migrateAccount)
                .collect(Collectors.toUnmodifiableList()).toArray(new CompletableFuture[]{}));

        migrationTask
                .thenApply((ignored) -> Duration.between(start, LocalDateTime.now()))
                .thenAccept((duration) -> log.info("Migration is completed in {} minutes and {} seconds",
                       duration.toMinutes(), duration.toSecondsPart()));

    }

    public void migrateAccount(String name) {
        migrationRunner.migrateAccount(mongoRepository.findAccountDocumentByLogin(name));
    }


}
