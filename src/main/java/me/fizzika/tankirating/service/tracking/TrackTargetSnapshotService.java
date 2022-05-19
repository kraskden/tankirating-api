package me.fizzika.tankirating.service.tracking;

import me.fizzika.tankirating.dto.tracking.TrackSnapshotDTO;
import me.fizzika.tankirating.enums.TrackFormat;

public interface TrackTargetSnapshotService {

    TrackSnapshotDTO getLatestSnapshot(Integer targetId, TrackFormat format);

    TrackSnapshotDTO getInitSnapshot(Integer targetId, TrackFormat format);

}
