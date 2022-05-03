package me.fizzika.tankirating.service;

import me.fizzika.tankirating.dto.tracking.TrackSnapshotDTO;
import me.fizzika.tankirating.enums.TrackFormat;

public interface AccountSnapshotService {

    TrackSnapshotDTO getLatestSnapshot(String nickname, TrackFormat format);

    TrackSnapshotDTO getInitSnapshot(String nickname, TrackFormat format);

}
