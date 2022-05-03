package me.fizzika.tankirating.dto.tracking;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TrackSnapshotDTO {

    private LocalDateTime timestamp;

    @JsonUnwrapped
    private TrackingDTO tracking;

}
