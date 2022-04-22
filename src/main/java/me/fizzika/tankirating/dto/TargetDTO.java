package me.fizzika.tankirating.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.fizzika.tankirating.enums.TrackTargetType;

@Data
@AllArgsConstructor
public class TargetDTO {

    private String name;

    private TrackTargetType type;

}
