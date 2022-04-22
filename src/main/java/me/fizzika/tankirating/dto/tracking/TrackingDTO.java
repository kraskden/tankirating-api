package me.fizzika.tankirating.dto.tracking;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;
import me.fizzika.tankirating.dto.TargetDTO;
import me.fizzika.tankirating.dto.tracking.track_data.TrackData;

import java.time.LocalDateTime;

@Data
public abstract class TrackingDTO<T extends TrackData> {

    private TargetDTO target;

    private LocalDateTime timestamp;

    @JsonUnwrapped
    T track;

}
