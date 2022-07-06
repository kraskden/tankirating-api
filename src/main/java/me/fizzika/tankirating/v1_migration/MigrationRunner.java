package me.fizzika.tankirating.v1_migration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.v1_migration.service.V1MigrationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MigrationRunner {

    private final List<V1MigrationService> services;

    public void migrate() {
        for (V1MigrationService service : services) {
            log.info("Starting migration for service: {}", service.getClass().getSimpleName());
            service.migrate();
            log.info("Ended migration for service: {}", service.getClass().getSimpleName());
        }
    }

}
