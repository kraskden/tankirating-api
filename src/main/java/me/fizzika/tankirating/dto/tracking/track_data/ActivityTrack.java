package me.fizzika.tankirating.dto.tracking.track_data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO: Refactor this shit
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityTrack  {

    private String name;

    private int score;

    private long time;

}
