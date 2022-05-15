package me.fizzika.tankirating.v1_migration.record.online;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class OnlineStampSchema {

    LocalDateTime timestamp;

    Integer online;

    Integer inbattles;

}
