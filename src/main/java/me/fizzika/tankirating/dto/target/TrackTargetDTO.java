package me.fizzika.tankirating.dto.target;

import lombok.Data;
import me.fizzika.tankirating.enums.track.TrackTargetStatus;
import me.fizzika.tankirating.enums.track.TrackTargetType;
import me.fizzika.tankirating.model.validation.Create;
import me.fizzika.tankirating.model.validation.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

@Data
public class TrackTargetDTO {

    @Null(groups = {Create.class, Update.class})
    private Integer id;

    @NotBlank(groups = {Create.class, Update.class})
    private String name;

    @Null(groups = {Create.class, Update.class})
    private TrackTargetType type;

    @Null(groups = Create.class)
    private TrackTargetStatus status;

    public TrackTargetDTO(Integer id, String name, TrackTargetType type) {
        this.name = name;
        this.id = id;
        this.type = type;
    }
}
