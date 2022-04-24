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
public class TrackFullModel implements TrackModel<TrackFullModel> {

    private TrackBaseModel base;

    private Map<String, TrackUsageModel> supplies;

    private Map<TrackActivityType, TrackActivityModel> activities;

    {
        supplies = new HashMap<>();
        activities = new EnumMap<>(TrackActivityType.class);
        Arrays.stream(TrackActivityType.values()).forEach(t -> activities.put(t, new TrackActivityModel()));
    }

    public TrackFullModel(TrackFullModel model) {
        add(model);
    }

    @Override
    public void add(TrackFullModel other) {
        base.add(other.base);
        TrackUtils.addMap(supplies, other.supplies, TrackUsageModel::new);
        TrackUtils.addMap(activities, other.activities, TrackActivityModel::new);
    }

}
