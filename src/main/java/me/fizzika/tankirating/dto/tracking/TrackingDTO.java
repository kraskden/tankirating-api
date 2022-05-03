package me.fizzika.tankirating.dto.tracking;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import me.fizzika.tankirating.dto.TrackTargetDTO;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TrackingDTO {

    private TrackTargetDTO target;

    private int gold;

    private int kills;

    private int deaths;

    private int cry;

    private int score;

    private int premiumDays;

    private long time;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TrackActivitiesDTO activities;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<TrackSupplyDTO> supplies;

}
