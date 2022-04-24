package me.fizzika.tankirating.model.tracking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.fizzika.tankirating.model.TrackModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackPlayModel implements TrackModel<TrackPlayModel> {

    private int score;
    private long time;

    public TrackPlayModel(TrackPlayModel model) {
        add(model);
    }

    @Override
    public void add(TrackPlayModel other) {
        score += other.score;
        time += other.time;
    }

}
