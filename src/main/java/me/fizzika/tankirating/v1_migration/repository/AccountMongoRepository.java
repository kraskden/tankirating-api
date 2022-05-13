package me.fizzika.tankirating.v1_migration.repository;

import me.fizzika.tankirating.v1_migration.record.tracking.AccountDocument;

import java.util.List;

public interface AccountMongoRepository {

    List<String> getAccountLogins();

    AccountDocument findAccountDocumentByLogin(String login);

}
