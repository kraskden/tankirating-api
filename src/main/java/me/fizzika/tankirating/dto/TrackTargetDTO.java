package me.fizzika.tankirating.dto;

import lombok.Data;
import me.fizzika.tankirating.enums.track.TrackTargetType;

@Data
public class TrackTargetDTO {

    private Integer id;

    private String name;

    private TrackTargetType type;

    public TrackTargetDTO(Integer id, String name) {
        this.name = name;
        this.id = id;
        this.type = TrackTargetType.fromName(name);
    }

}
