package me.fizzika.tankirating.v1_migration.record.tracking;

import lombok.Value;
import me.fizzika.tankirating.v1_migration.enums.V1TrackType;

import java.time.LocalDateTime;
import java.util.List;

@Value
public class TrackingSchema {

    LocalDateTime timestamp;
    V1TrackType trackType;
    Integer golds, kills, deaths, cry, score, time;
    Boolean hasPremium;

    List<TrackingActivitySchema> activities;
    List<TrackSupplySchema> supplies;

}
