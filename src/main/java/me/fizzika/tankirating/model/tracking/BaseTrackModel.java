package me.fizzika.tankirating.model.tracking;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.fizzika.tankirating.model.TrackModel;

@Data
@NoArgsConstructor
public class BaseTrackModel implements TrackModel<BaseTrackModel> {

    private int gold;

    private int kills;

    private int deaths;

    private int cry;

    private int score;

    private long time;

    public BaseTrackModel(BaseTrackModel model) {
        add(model);
    }

    @Override
    public void add(BaseTrackModel other) {
        gold += other.gold;
        kills += other.kills;
        deaths += other.deaths;
        cry += other.cry;
        score += other.score;
        time += other.time;
    }

}
