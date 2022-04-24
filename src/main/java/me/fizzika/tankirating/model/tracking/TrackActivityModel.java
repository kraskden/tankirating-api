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
public class TrackActivityModel implements TrackModel<TrackActivityModel> {

    private Map<String, TrackPlayModel> playTracks = new HashMap<>();

    public TrackActivityModel(TrackActivityModel model) {
        add(model);
    }

    @Override
    public void add(TrackActivityModel other) {
        TrackUtils.addMap(playTracks, other.playTracks, TrackPlayModel::new);
    }

}
