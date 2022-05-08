package me.fizzika.tankirating.v1_migration.repository.impl;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.v1_migration.record.tracking.AccountDocument;
import me.fizzika.tankirating.v1_migration.repository.AccountMongoTemplateRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AccountMongoTemplateRepositoryImpl implements AccountMongoTemplateRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<String> getAccountLogins() {
        Query q = new Query();
        q.fields().include("login");

        return mongoTemplate.find(q, AccountDocument.class).stream()
                .map(AccountDocument::getLogin)
                .collect(Collectors.toList());
    }
}
