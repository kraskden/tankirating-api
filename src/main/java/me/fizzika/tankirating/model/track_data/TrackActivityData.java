package me.fizzika.tankirating.model.track_data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.fizzika.tankirating.model.TrackData;
import me.fizzika.tankirating.util.TrackUtils;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackActivityData implements TrackData<TrackActivityData> {

    private Map<String, TrackPlayData> playTracks = new HashMap<>();

    public TrackActivityData(TrackActivityData model) {
        add(model);
    }

    @Override
    public void add(TrackActivityData other) {
        TrackUtils.mergeMapValues(playTracks, other.playTracks, TrackData::add, TrackPlayData::new);
    }

    @Override
    public void sub(TrackActivityData other) {
        TrackUtils.mergeMapValues(playTracks, other.playTracks, TrackData::sub, TrackPlayData::new);
    }

    @Override
    public TrackActivityData copy() {
        return new TrackActivityData(this);
    }

}
