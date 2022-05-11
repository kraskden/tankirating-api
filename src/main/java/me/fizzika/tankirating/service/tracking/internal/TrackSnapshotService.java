package me.fizzika.tankirating.service.tracking.internal;

import me.fizzika.tankirating.model.TrackSnapshot;
import me.fizzika.tankirating.util.Pair;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TrackSnapshotService {

    void save(TrackSnapshot snapshot);

    boolean exists(Integer targetId, LocalDateTime timestamp);

    Optional<TrackSnapshot> findFirstInRange(Integer targetId, LocalDateTime from, LocalDateTime to);

    Optional<TrackSnapshot> findLastInRange(Integer targetId, LocalDateTime from, LocalDateTime to);

    Optional<Pair<TrackSnapshot>> findBorderSnapshots(Integer targetId, LocalDateTime from, LocalDateTime to);

    void deleteAllInRange(LocalDateTime from, LocalDateTime to);

}
