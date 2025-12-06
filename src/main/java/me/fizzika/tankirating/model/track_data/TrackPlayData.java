package me.fizzika.tankirating.model.track_data;

import static java.util.Comparator.comparing;

import java.util.Comparator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.fizzika.tankirating.model.TrackData;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackPlayData implements TrackData<TrackPlayData>, Comparable<TrackPlayData> {

    private long score;
    private long time;

    public TrackPlayData(TrackPlayData model) {
        add(model);
    }

    @Override
    public void add(TrackPlayData other) {
        score += other.score;
        time += other.time;
    }

    @Override
    public void sub(TrackPlayData other) {
        score -= other.score;
        time -= other.time;
    }

    @Override
    public TrackPlayData copy() {
        return new TrackPlayData(this);
    }

    @Override
    public String toString() {
        return String.format("(s: %d, t: %d)", score, time);
    }

    public boolean isEmpty() {
        return time == 0 && score == 0;
    }

    @Override
    public int compareTo(TrackPlayData other) {
        return comparing(TrackPlayData::getScore).thenComparingLong(TrackPlayData::getTime)
                .compare(this, other);
    }
}