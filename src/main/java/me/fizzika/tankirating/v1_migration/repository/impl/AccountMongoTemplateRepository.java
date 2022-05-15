package me.fizzika.tankirating.v1_migration.repository.impl;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.v1_migration.record.tracking.AccountDocument;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@Profile("migration")
@RequiredArgsConstructor
public class AccountMongoTemplateRepository {

    private final MongoTemplate mongoTemplate;

    public List<String> getAccountLogins() {
        Query q = new Query();
        q.fields().include("login");

        return mongoTemplate.find(q, AccountDocument.class).stream()
                .map(AccountDocument::getLogin)
                .collect(Collectors.toList());
    }
}
