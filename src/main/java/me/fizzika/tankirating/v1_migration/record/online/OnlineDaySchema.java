package me.fizzika.tankirating.v1_migration.record.online;

import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
public class OnlineDaySchema {

    LocalDateTime date;

    OnlineDayStatSchema online;

    OnlineDayStatSchema inbattles;

    List<OnlineStampSchema> track;

}
