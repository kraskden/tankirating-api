package me.fizzika.tankirating.v1_migration.record.online;

import lombok.Value;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "onlines")
@Value
public class OnlineDocument {

    LocalDateTime lastUpdate;

    LocalDateTime lastClear;

    OnlineDaySchema today;

    List<OnlineDaySchema> days;

}
