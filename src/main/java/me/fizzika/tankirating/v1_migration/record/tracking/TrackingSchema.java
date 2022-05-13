package me.fizzika.tankirating.v1_migration.record.tracking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import lombok.Value;
import me.fizzika.tankirating.v1_migration.enums.V1TrackType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class TrackingSchema {

    LocalDateTime timestamp;
    V1TrackType trackType;
    Integer golds, kills, deaths, cry, score, time;
    Boolean hasPremium;

    List<TrackingActivitySchema> activities;
    List<TrackSupplySchema> supplies;

}
