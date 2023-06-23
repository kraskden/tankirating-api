package me.fizzika.tankirating.dto.management;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.fizzika.tankirating.enums.track.TrackTargetStatus;

@Data
@AllArgsConstructor
public class TargetStatEntryDTO {

    private TrackTargetStatus status;
    private Long count;
}