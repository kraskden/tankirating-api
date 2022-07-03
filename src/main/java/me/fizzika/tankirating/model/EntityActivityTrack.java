package me.fizzika.tankirating.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EntityActivityTrack {

    private Short entityId;

    private Long time;

    private Long score;

}
