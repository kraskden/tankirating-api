package me.fizzika.tankirating.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.fizzika.tankirating.model.track_data.TrackFullData;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class TrackSnapshot {

    private UUID targetId;

    private LocalDateTime timestamp;

    private TrackFullData trackData;

}
