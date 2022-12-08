package me.fizzika.tankirating.service.tracking;

import me.fizzika.tankirating.dto.tracking.TrackSnapshotDTO;
import me.fizzika.tankirating.enums.track.TrackFormat;

import java.time.LocalDate;

public interface TrackTargetSnapshotService {

    TrackSnapshotDTO getLatestSnapshot(Integer targetId, TrackFormat format);

    TrackSnapshotDTO getInitSnapshot(Integer targetId, TrackFormat format);

    TrackSnapshotDTO getSnapshotForDate(Integer targetId, LocalDate date, TrackFormat format);
}
