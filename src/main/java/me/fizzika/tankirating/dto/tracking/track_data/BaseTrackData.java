package me.fizzika.tankirating.dto.tracking.track_data;

import lombok.Data;

@Data
public class BaseTrackData implements TrackData {

    private int gold;

    private int kills;

    private int deaths;

    private int cry;

    private int score;

    private long time;

}
