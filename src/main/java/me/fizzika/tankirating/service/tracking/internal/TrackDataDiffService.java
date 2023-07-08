package me.fizzika.tankirating.service.tracking.internal;

import me.fizzika.tankirating.model.TrackSnapshot;
import me.fizzika.tankirating.model.date.DatePeriod;
import me.fizzika.tankirating.model.track_data.TrackFullData;

public interface TrackDataDiffService {

    TrackFullData diff(Integer targetId, TrackFullData endData, TrackFullData startData, DatePeriod diffDates);
    TrackFullData diff(TrackSnapshot end, TrackSnapshot start);
}
