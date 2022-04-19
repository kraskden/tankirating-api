package me.fizzika.tankirating.dto.tracking;

import lombok.Data;
import me.fizzika.tankirating.dto.TargetDTO;
import me.fizzika.tankirating.dto.tracking.track.ActivityTrackDTO;
import me.fizzika.tankirating.dto.tracking.track.SupplyTrackDTO;
import me.fizzika.tankirating.enums.TrackActivityType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class BaseTrackingDTO {

    private TargetDTO target;

    private LocalDateTime timestamp;

    private Integer gold;

    private Integer kills;

    private Integer deaths;

    private Integer cry;

    private Integer score;

    private Long time;

}
