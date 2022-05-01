package me.fizzika.tankirating.repository;

import me.fizzika.tankirating.record.tracking.TrackSnapshotRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrackSnapshotRepository extends JpaRepository<TrackSnapshotRecord, UUID> {

    Optional<TrackSnapshotRecord> findOneByTargetIdAndTimestamp(UUID targetId, LocalDateTime timestamp);

    List<TrackSnapshotRecord> findAllByTargetIdAndTimestampIn(UUID targetId, Collection<LocalDateTime> stamps);

    boolean existsByTargetIdAndTimestamp(UUID targetId, LocalDateTime timestamp);

    void deleteByTimestampBetween(LocalDateTime start, LocalDateTime end);

    Optional<TrackSnapshotRecord> findFirstByTargetIdAndTimestampBetweenOrderByTimestampAsc(UUID targetId,
                                                                                           LocalDateTime from,
                                                                                           LocalDateTime to);

    default Optional<TrackSnapshotRecord> findClosestSnapshot(UUID targetId, LocalDateTime from, LocalDateTime to) {
        return findFirstByTargetIdAndTimestampBetweenOrderByTimestampAsc(targetId, from, to);
    }

}
