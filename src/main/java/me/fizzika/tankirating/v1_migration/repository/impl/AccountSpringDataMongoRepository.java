package me.fizzika.tankirating.v1_migration.repository.impl;

import me.fizzika.tankirating.v1_migration.record.tracking.AccountDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AccountSpringDataMongoRepository extends MongoRepository<AccountDocument, String> {

    AccountDocument findAccountDocumentByLogin(String login);

}
