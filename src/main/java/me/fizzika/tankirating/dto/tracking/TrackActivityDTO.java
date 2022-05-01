package me.fizzika.tankirating.dto.tracking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TrackActivityDTO {

    private String name;

    private int score;

    private long time;

}