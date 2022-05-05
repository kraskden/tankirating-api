package me.fizzika.tankirating.v1_migration.record;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "accounts")
@Data
public class AccountDocument {

    @Id
    private String accountId;

    private String login;

}
