package me.fizzika.tankirating.model.track_data;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.fizzika.tankirating.model.TrackData;

@Data
@NoArgsConstructor
public class TrackBaseData implements TrackData<TrackBaseData> {

    private int gold;

    private int kills;

    private int deaths;

    private int cry;

    private int score;

    private long time;

    public TrackBaseData(TrackBaseData model) {
        add(model);
    }

    @Override
    public void add(TrackBaseData other) {
        gold += other.gold;
        kills += other.kills;
        deaths += other.deaths;
        cry += other.cry;
        score += other.score;
        time += other.time;
    }

    @Override
    public void sub(TrackBaseData other) {
        gold -= other.gold;
        kills -= other.kills;
        deaths -= other.deaths;
        cry -= other.cry;
        score -= other.score;
        time -= other.time;
    }

    @Override
    public TrackBaseData copy() {
        return new TrackBaseData(this);
    }

}
