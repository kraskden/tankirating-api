package me.fizzika.tankirating.dto.tracking;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.fizzika.tankirating.dto.PeriodTrackDTO;
import me.fizzika.tankirating.enums.PeriodUnit;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class TrackDiffDTO extends PeriodTrackDTO {

    @JsonUnwrapped
    private TrackingDTO tracking;

    private int premiumDays;

    private Integer maxScore;

}
