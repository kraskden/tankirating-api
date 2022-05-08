package me.fizzika.tankirating.v1_migration.record.tracking;

import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "accounts")
@Value
public class AccountDocument {

    @Id
    String accountId;

    String login;

    List<TrackingSchema> tracking, daily, weekly, monthly;

    TrackingSchema currWeek, currMonth;

}