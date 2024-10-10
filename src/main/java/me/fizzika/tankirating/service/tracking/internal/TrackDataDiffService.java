package me.fizzika.tankirating.service.tracking.internal;

import me.fizzika.tankirating.model.TrackSnapshot;
import me.fizzika.tankirating.model.date.DatePeriod;
import me.fizzika.tankirating.model.date.DateRange;
import me.fizzika.tankirating.model.track_data.TrackFullData;

public interface TrackDataDiffService {

    TrackFullData diff(TrackSnapshot end, TrackSnapshot start);
    TrackFullData diff(TrackFullData endData, TrackFullData startData, DateRange dateRange);

    boolean strictDiffMode(DatePeriod datePeriod);
}