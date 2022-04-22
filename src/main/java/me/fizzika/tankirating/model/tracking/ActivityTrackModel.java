package me.fizzika.tankirating.model.tracking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.fizzika.tankirating.model.TrackModel;
import me.fizzika.tankirating.util.TrackUtils;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityTrackModel implements TrackModel<ActivityTrackModel> {

    private Map<String, PlayTrackModel> playTracks = new HashMap<>();

    public ActivityTrackModel(ActivityTrackModel model) {
        add(model);
    }

    @Override
    public void add(ActivityTrackModel other) {
        TrackUtils.addMap(playTracks, other.playTracks, PlayTrackModel::new);
    }

}
