package me.fizzika.tankirating.service.tracking.internal;

import me.fizzika.tankirating.model.date.DatePeriod;
import me.fizzika.tankirating.model.track_data.TrackFullData;

public interface TrackDataDiffService {

    TrackFullData diff(Integer targetId, TrackFullData currentData, TrackFullData snapshotData, DatePeriod diffDates);
}
