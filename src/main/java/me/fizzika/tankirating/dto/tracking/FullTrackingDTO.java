package me.fizzika.tankirating.dto.tracking;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.fizzika.tankirating.dto.tracking.track.ActivityTrackDTO;
import me.fizzika.tankirating.dto.tracking.track.SupplyTrackDTO;
import me.fizzika.tankirating.enums.TrackActivityType;

import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class FullTrackingDTO extends BaseTrackingDTO {

    private Map<TrackActivityType, List<ActivityTrackDTO>> activities;

    private List<SupplyTrackDTO> supplies;

}
