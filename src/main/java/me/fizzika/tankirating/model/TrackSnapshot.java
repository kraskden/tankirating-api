package me.fizzika.tankirating.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.fizzika.tankirating.enums.SnapshotPeriod;
import me.fizzika.tankirating.model.track_data.TrackFullData;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TrackSnapshot {

    private Integer targetId;
    private SnapshotPeriod period;
    private LocalDateTime timestamp;
    private TrackFullData trackData;
    private Boolean hasPremium;
}