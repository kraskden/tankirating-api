package me.fizzika.tankirating.v1_migration.repository;

import me.fizzika.tankirating.v1_migration.record.online.OnlineDocument;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

@Profile("migration")
public interface OnlineMongoRepository extends MongoRepository<OnlineDocument, String> {

    default Optional<OnlineDocument> findFirst() {
        var lst = findAll();
        if (lst.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(lst.get(0));
        }
    }

}
