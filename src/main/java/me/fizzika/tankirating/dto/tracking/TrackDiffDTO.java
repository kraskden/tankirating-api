package me.fizzika.tankirating.dto.tracking;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;
import me.fizzika.tankirating.enums.track.TrackDiffPeriod;

import java.time.LocalDateTime;

@Data
public class TrackDiffDTO {

    @JsonUnwrapped
    private TrackingDTO tracking;

    private TrackDiffPeriod period;

    private LocalDateTime periodStart;

    private LocalDateTime periodEnd;

    private LocalDateTime trackStart;

    private LocalDateTime trackEnd;

}
