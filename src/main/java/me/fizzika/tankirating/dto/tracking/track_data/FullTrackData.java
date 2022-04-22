package me.fizzika.tankirating.dto.tracking.track_data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.fizzika.tankirating.enums.TrackActivityType;

import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class FullTrackData extends BaseTrackData {

    private Map<TrackActivityType, List<ActivityTrack>> activities;

    private List<SupplyTrack> supplies;

}
