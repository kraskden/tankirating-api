package me.fizzika.tankirating.model.track_data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.fizzika.tankirating.model.TrackData;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackUsageData implements TrackData<TrackUsageData> {

    private int usages;

    public TrackUsageData(TrackUsageData model) {
        add(model);
    }

    @Override
    public void add(TrackUsageData other) {
        usages += other.usages;
    }

    @Override
    public void sub(TrackUsageData other) {
        usages -= other.usages;
    }

    @Override
    public TrackUsageData copy() {
        return new TrackUsageData(this);
    }

    public boolean isEmpty() {
        return usages == 0;
    }

    @Override
    public String toString() {
        return String.format("(%d)", usages);
    }

}