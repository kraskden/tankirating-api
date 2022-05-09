package me.fizzika.tankirating.dto.tracking;

import lombok.Data;
import me.fizzika.tankirating.enums.track.TankiEntityType;

@Data
public class TrackEntityDTO {

    private Short id;

    private String name;

    private TankiEntityType type;

}
