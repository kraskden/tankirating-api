package me.fizzika.tankirating.dto.tracking;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import me.fizzika.tankirating.dto.TrackTargetDTO;

import java.util.List;

@Data
public class TrackingDTO {

    private int targetId;

    private int gold;

    private int kills;

    private int deaths;

    private int cry;

    private int score;

    private long time;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TrackActivitiesDTO activities;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<TrackUsageDTO> supplies;

}
