package me.fizzika.tankirating.service.tracking;

import me.fizzika.tankirating.dto.TrackTargetDTO;
import me.fizzika.tankirating.dto.tracking.TrackSnapshotDTO;
import me.fizzika.tankirating.enums.TrackFormat;

public interface TrackTargetSnapshotService {

    TrackSnapshotDTO getLatestSnapshot(TrackTargetDTO target, TrackFormat format);

    TrackSnapshotDTO getInitSnapshot(TrackTargetDTO target, TrackFormat format);

}
