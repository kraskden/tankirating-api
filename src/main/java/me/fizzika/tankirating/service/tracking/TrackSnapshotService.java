package me.fizzika.tankirating.service.tracking;

import me.fizzika.tankirating.model.TrackSnapshot;
import me.fizzika.tankirating.util.Pair;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface TrackSnapshotService {

    void saveSnapshot(TrackSnapshot snapshot);

    boolean existsSnapshot(UUID targetId, LocalDateTime timestamp);

    Optional<TrackSnapshot> findClosestSnapshot(UUID targetId, LocalDateTime from, LocalDateTime to);

    Optional<Pair<TrackSnapshot>> findBorderSnapshots(UUID targetId, LocalDateTime from, LocalDateTime to);

    void deleteAllSnapshots(LocalDateTime from, LocalDateTime to);

}
