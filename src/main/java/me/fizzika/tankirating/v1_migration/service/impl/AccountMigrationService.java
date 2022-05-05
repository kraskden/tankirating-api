package me.fizzika.tankirating.v1_migration.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.v1_migration.record.AccountDocument;
import me.fizzika.tankirating.v1_migration.repository.AccountMongoRepository;
import me.fizzika.tankirating.v1_migration.service.V1MigrationService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountMigrationService implements V1MigrationService {

    private final AccountMongoRepository mongoRepository;

    @Override
    @PostConstruct
    public void migrate() {
        List<AccountDocument> documents = mongoRepository.findAll();
        log.warn("Documents: {}", documents);
    }

}
