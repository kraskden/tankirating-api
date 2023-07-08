package me.fizzika.tankirating.model.activity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EntityNameActivityTrack {

    private String name;
    private Long time;
    private Long score;
}
