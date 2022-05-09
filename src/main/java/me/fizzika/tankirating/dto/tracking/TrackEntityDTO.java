package me.fizzika.tankirating.dto.tracking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.fizzika.tankirating.enums.track.TankiEntityType;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TrackEntityDTO {

    private Short id;

    private String name;

    private TankiEntityType type;

}
