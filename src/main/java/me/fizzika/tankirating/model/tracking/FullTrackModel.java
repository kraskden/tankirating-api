package me.fizzika.tankirating.model.tracking;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.fizzika.tankirating.enums.TrackActivityType;
import me.fizzika.tankirating.model.TrackModel;
import me.fizzika.tankirating.util.TrackUtils;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class FullTrackModel implements TrackModel<FullTrackModel> {

    private BaseTrackModel baseTrack;

    private Map<String, UsageTrackModel> suppliesUsage;

    private Map<TrackActivityType, ActivityTrackModel> activities;

    {
        suppliesUsage = new HashMap<>();
        activities = new EnumMap<>(TrackActivityType.class);
        Arrays.stream(TrackActivityType.values()).forEach(t -> activities.put(t, new ActivityTrackModel()));
    }

    public FullTrackModel(FullTrackModel model) {
        add(model);
    }

    @Override
    public void add(FullTrackModel other) {
        baseTrack.add(other.baseTrack);
        TrackUtils.addMap(suppliesUsage, other.suppliesUsage, UsageTrackModel::new);
        TrackUtils.addMap(activities, other.activities, ActivityTrackModel::new);
    }

}
