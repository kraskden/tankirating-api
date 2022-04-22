package me.fizzika.tankirating.model.tracking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.fizzika.tankirating.model.TrackModel;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayTrackModel implements TrackModel<PlayTrackModel> {

    private int score;
    private long time;

    public PlayTrackModel(PlayTrackModel model) {
        add(model);
    }

    @Override
    public void add(PlayTrackModel other) {
        score += other.score;
        time += other.time;
    }

}
