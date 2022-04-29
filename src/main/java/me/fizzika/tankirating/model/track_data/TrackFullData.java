package me.fizzika.tankirating.model.track_data;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.fizzika.tankirating.enums.TrackActivityType;
import me.fizzika.tankirating.model.TrackData;
import me.fizzika.tankirating.util.TrackUtils;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class TrackFullData implements TrackData<TrackFullData> {

    private TrackBaseData base;

    private Map<String, TrackUsageData> supplies;

    private Map<TrackActivityType, TrackActivityData> activities;

    {
        base = new TrackBaseData();
        supplies = new HashMap<>();
        activities = new EnumMap<>(TrackActivityType.class);
        Arrays.stream(TrackActivityType.values()).forEach(t -> activities.put(t, new TrackActivityData()));
    }

    public TrackFullData(TrackFullData model) {
        add(model);
    }

    @Override
    public void add(TrackFullData other) {
        base.add(other.base);
        TrackUtils.mergeMap(supplies, other.supplies, TrackData::add, TrackUsageData::new);
        TrackUtils.mergeMap(activities, other.activities, TrackData::add, TrackActivityData::new);
    }

    @Override
    public void sub(TrackFullData other) {
        base.sub(other.base);
        TrackUtils.mergeMap(supplies, other.supplies, TrackData::sub, TrackUsageData::new);
        TrackUtils.mergeMap(activities, other.activities, TrackData::sub, TrackActivityData::new);
    }

    @Override
    public TrackFullData copy() {
        return new TrackFullData(this);
    }

    public boolean valid() {
        return base.getTime() >= 0;
    }

    public boolean notEmpty() {
        return base.getTime() > 0;
    }

}
