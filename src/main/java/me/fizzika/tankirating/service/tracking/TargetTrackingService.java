package me.fizzika.tankirating.service.tracking;

import me.fizzika.tankirating.model.track_data.TrackFullData;

/**
 * Calculate and persist snapshots and diffs to the database.
 */
public interface TargetTrackingService {

    void updateTargetData(Integer targetId, TrackFullData currentData);

}
