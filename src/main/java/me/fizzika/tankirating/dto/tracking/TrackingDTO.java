package me.fizzika.tankirating.dto.tracking;

import lombok.Data;
import me.fizzika.tankirating.dto.TargetDTO;

import java.time.LocalDateTime;

@Data
public abstract class TrackingDTO {

    private TargetDTO target;

    private LocalDateTime timestamp;

}
