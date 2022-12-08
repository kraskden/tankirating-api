package me.fizzika.tankirating.v1_migration.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.v1_migration.MigrationRunner;
import me.fizzika.tankirating.v1_migration.service.impl.AccountMigrationService;
import me.fizzika.tankirating.v1_migration.service.impl.GroupMigrationService;
import me.fizzika.tankirating.v1_migration.service.impl.OnlineMigrationService;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("migration")
@Tag(name = "V1 migration", description = "Migrate data from mongodb")
@RequestMapping("/migration")
@RequiredArgsConstructor
public class MigrationTestController {

    private final AccountMigrationService accountMigrationService;
    private final OnlineMigrationService onlineMigrationService;
    private final GroupMigrationService groupMigrationService;

    private final MigrationRunner migrationRunner;

    @PostMapping("/account")
    public void migrateAllAccounts() {
        accountMigrationService.migrate();
    }

    @PostMapping("/online")
    public void migrateOnline() {
        onlineMigrationService.migrate();
    }

    @PostMapping("/account/{name}")
    public void migrateOneAccount(@PathVariable String name) {
        accountMigrationService.migrateAccount(name);
    }

    @PostMapping("/group")
    public void migrateGroups() {
        groupMigrationService.migrate();
    }

    @PostMapping("/all")
    public void migrateAll() {
        migrationRunner.migrate();
    }

}
