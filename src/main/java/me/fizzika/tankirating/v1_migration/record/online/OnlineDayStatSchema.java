package me.fizzika.tankirating.v1_migration.record.online;

import lombok.Value;

@Value
public class OnlineDayStatSchema {

    Integer pcu;

    Integer avg;

    Integer sigma;

}
