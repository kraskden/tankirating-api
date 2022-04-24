package me.fizzika.tankirating.model.tracking;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.fizzika.tankirating.model.TrackModel;

@Data
@NoArgsConstructor
public class TrackBaseModel implements TrackModel<TrackBaseModel> {

    private int gold;

    private int kills;

    private int deaths;

    private int cry;

    private int score;

    private long time;

    public TrackBaseModel(TrackBaseModel model) {
        add(model);
    }

    @Override
    public void add(TrackBaseModel other) {
        gold += other.gold;
        kills += other.kills;
        deaths += other.deaths;
        cry += other.cry;
        score += other.score;
        time += other.time;
    }

}
