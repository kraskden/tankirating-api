package me.fizzika.tankirating.service.tracking.internal;

import me.fizzika.tankirating.enums.PeriodUnit;
import me.fizzika.tankirating.model.TrackGroup;
import me.fizzika.tankirating.model.date.PeriodDiffDates;
import me.fizzika.tankirating.model.track_data.TrackFullData;

/**
 * Calculate and persist snapshots&diffs into the database.
 */
public interface TrackStoreService {

    void updateTargetData(Integer targetId, TrackFullData currentData, boolean hasPremium);

    void updateCurrentGroupData(TrackGroup group);

    void updateGroupDiff(TrackGroup group, PeriodUnit diffPeriod, PeriodDiffDates diffDates);

}
