package me.fizzika.tankirating.repository;

import me.fizzika.tankirating.record.tracking.TrackSnapshotRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SnapshotRecordRepository extends JpaRepository<TrackSnapshotRecord, UUID> {

    Optional<TrackSnapshotRecord> findByTrackDataTargetIdAndTrackDataTimestamp(UUID targetId, LocalDateTime timestamp);

}
