package me.fizzika.tankirating.dto;

import lombok.Data;
import me.fizzika.tankirating.enums.track.TrackTargetStatus;
import me.fizzika.tankirating.enums.track.TrackTargetType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

@Data
public class TrackTargetDTO {

    @Null
    private Integer id;

    @NotBlank
    private String name;

    @Null
    private TrackTargetType type;

    private TrackTargetStatus status = TrackTargetStatus.ACTIVE;

    public TrackTargetDTO(Integer id, String name, TrackTargetType type) {
        this.name = name;
        this.id = id;
        this.type = type;
    }

}
