package me.fizzika.tankirating.dto;

import lombok.Data;
import me.fizzika.tankirating.enums.TargetType;

@Data
public class TargetDTO {

    private String name;

    private TargetType type;

}
