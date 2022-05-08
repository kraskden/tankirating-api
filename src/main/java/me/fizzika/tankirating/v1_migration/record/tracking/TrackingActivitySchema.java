package me.fizzika.tankirating.v1_migration.record.tracking;

import lombok.Value;
import me.fizzika.tankirating.enums.track.TrackActivityType;

@Value
public class TrackingActivitySchema {

    String name;
    Integer score, time;

    String role;

}
