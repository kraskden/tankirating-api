package me.fizzika.tankirating.dto;

import lombok.Data;
import me.fizzika.tankirating.enums.TrackTargetType;

import java.util.UUID;

@Data
public class TrackTargetDTO {

    private UUID id;

    private String name;

    private TrackTargetType type;

    public TrackTargetDTO(UUID id, String name) {
        this.name = name;
        this.id = id;
        this.type = TrackTargetType.fromName(name);
    }

}
