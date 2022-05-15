package me.fizzika.tankirating.v1_migration.controller;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.v1_migration.service.impl.AccountMigrationService;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("migration")
@RequestMapping("/migration")
@RequiredArgsConstructor
public class MigrationTestController {

    private final AccountMigrationService accountMigrationService;

    @PostMapping
    public void migrateAll() {
        accountMigrationService.migrate();
    }

    @PostMapping("/{name}")
    public void migrateOne(@PathVariable String name) {
        accountMigrationService.migrateAccount(name);
    }

}
