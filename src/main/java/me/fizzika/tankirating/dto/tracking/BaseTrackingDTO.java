package me.fizzika.tankirating.dto.tracking;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BaseTrackingDTO extends TrackingDTO {

    private int gold;

    private int kills;

    private int deaths;

    private int cry;

    private int score;

    private long time;

}
