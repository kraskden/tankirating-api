package me.fizzika.tankirating.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.fizzika.tankirating.enums.track.GroupMeta;

@Data
@AllArgsConstructor
public class TrackGroup {

    private Integer id;

    private GroupMeta meta;

}
