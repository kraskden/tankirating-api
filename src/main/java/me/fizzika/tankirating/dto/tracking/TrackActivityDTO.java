package me.fizzika.tankirating.dto.tracking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TrackActivityDTO {

    private String name;

    private long score;

    private long time;

}
