package me.fizzika.tankirating.model.tracking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.fizzika.tankirating.model.TrackModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsageTrackModel implements TrackModel<UsageTrackModel> {

    private long usage;

    public UsageTrackModel(UsageTrackModel model) {
        add(model);
    }

    @Override
    public void add(UsageTrackModel other) {
        usage += other.usage;
    }

}
