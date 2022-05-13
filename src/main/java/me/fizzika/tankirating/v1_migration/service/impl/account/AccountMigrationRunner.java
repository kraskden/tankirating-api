package me.fizzika.tankirating.v1_migration.service.impl.account;

import me.fizzika.tankirating.v1_migration.record.tracking.AccountDocument;

import java.util.concurrent.CompletableFuture;

public interface AccountMigrationRunner {

    CompletableFuture<Void> migrateAccount(AccountDocument account);

}
