package me.fizzika.tankirating.model.activity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EntityIdActivityTrack {

    private Short entityId;
    private Long time;
    private Long score;
}
