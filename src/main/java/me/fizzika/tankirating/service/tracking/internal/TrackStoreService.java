package me.fizzika.tankirating.service.tracking.internal;

import me.fizzika.tankirating.enums.DiffPeriodUnit;
import me.fizzika.tankirating.exceptions.tracking.InvalidTrackDataException;
import me.fizzika.tankirating.model.TrackGroup;
import me.fizzika.tankirating.model.date.PeriodDiffDates;
import me.fizzika.tankirating.model.track_data.TrackFullData;

/**
 * Calculate and persist snapshots&diffs into the database.
 */
public interface TrackStoreService {

    void updateTargetData(Integer targetId, TrackFullData currentData, boolean hasPremium)
        throws InvalidTrackDataException;

    void updateCurrentGroupData(TrackGroup group);

    void updateGroupDiff(TrackGroup group, DiffPeriodUnit diffPeriod, PeriodDiffDates diffDates);

}