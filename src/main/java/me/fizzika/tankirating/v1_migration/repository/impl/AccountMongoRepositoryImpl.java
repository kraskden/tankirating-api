package me.fizzika.tankirating.v1_migration.repository.impl;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.v1_migration.record.tracking.AccountDocument;
import me.fizzika.tankirating.v1_migration.repository.AccountMongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AccountMongoRepositoryImpl implements AccountMongoRepository {

    private final AccountMongoTemplateRepository templateRepository;
    private final AccountSpringDataMongoRepository springDataMongoRepository;

    @Override
    public List<String> getAccountLogins() {
        return templateRepository.getAccountLogins();
    }

    @Override
    public AccountDocument findAccountDocumentByLogin(String login) {
        return springDataMongoRepository.findAccountDocumentByLogin(login);
    }

}
