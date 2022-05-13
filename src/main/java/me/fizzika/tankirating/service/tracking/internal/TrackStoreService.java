package me.fizzika.tankirating.service.tracking.internal;

import me.fizzika.tankirating.model.track_data.TrackFullData;

/**
 * Calculate and persist snapshots&diffs into the database.
 */
public interface TrackStoreService {

    void updateTargetData(Integer targetId, TrackFullData currentData, boolean hasPremium);

}
