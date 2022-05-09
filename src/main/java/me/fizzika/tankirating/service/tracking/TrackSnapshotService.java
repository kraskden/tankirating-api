package me.fizzika.tankirating.service.tracking;

import me.fizzika.tankirating.model.TrackSnapshot;
import me.fizzika.tankirating.util.Pair;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TrackSnapshotService {

    void saveSnapshot(TrackSnapshot snapshot);

    boolean existsSnapshot(Integer targetId, LocalDateTime timestamp);

    Optional<TrackSnapshot> findClosestSnapshot(Integer targetId, LocalDateTime from, LocalDateTime to);

    Optional<Pair<TrackSnapshot>> findBorderSnapshots(Integer targetId, LocalDateTime from, LocalDateTime to);

    void deleteAllSnapshots(LocalDateTime from, LocalDateTime to);

}
