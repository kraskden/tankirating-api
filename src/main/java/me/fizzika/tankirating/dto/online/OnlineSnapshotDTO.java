package me.fizzika.tankirating.dto.online;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OnlineSnapshotDTO {

    private LocalDateTime timestamp;
    private Integer online;
    private Integer inbattles;

}
