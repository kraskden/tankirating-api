package me.fizzika.tankirating.model.tracking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.fizzika.tankirating.model.TrackModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackUsageModel implements TrackModel<TrackUsageModel> {

    private long usages;

    public TrackUsageModel(TrackUsageModel model) {
        add(model);
    }

    @Override
    public void add(TrackUsageModel other) {
        usages += other.usages;
    }

}
