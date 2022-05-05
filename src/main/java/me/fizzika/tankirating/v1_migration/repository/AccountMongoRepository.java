package me.fizzika.tankirating.v1_migration.repository;

import me.fizzika.tankirating.v1_migration.record.AccountDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountMongoRepository extends MongoRepository<AccountDocument, String> {
}
