package me.fizzika.tankirating.repository;

import me.fizzika.tankirating.model.TrackSnapshot;
import me.fizzika.tankirating.record.tracking.TrackSnapshotRecord;
import me.fizzika.tankirating.util.Pair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    Optional<TrackSnapshotRecord> findFirstByTargetIdAndTimestampGreaterThanEqualOrderByTimestampAsc(
            UUID targetId, LocalDateTime timestamp);

    Optional<TrackSnapshotRecord> findFirstByTargetIdAndTimestampLessThanEqualOrderByTimestampDesc(
            UUID targetId, LocalDateTime timestamp);

    default Optional<TrackSnapshotRecord> findClosestSnapshot(UUID targetId, LocalDateTime from, LocalDateTime to) {
        return findFirstByTargetIdAndTimestampBetweenOrderByTimestampAsc(targetId, from, to);
    }

    default Optional<Pair<TrackSnapshotRecord>> findBorderSnapshots(UUID targetId, LocalDateTime from,
                                                                    LocalDateTime to) {
        Optional<TrackSnapshotRecord> optStart = findFirstByTargetIdAndTimestampGreaterThanEqualOrderByTimestampAsc(
                targetId, from
        );
        Optional<TrackSnapshotRecord> optEnd = findFirstByTargetIdAndTimestampLessThanEqualOrderByTimestampDesc(
                targetId, to
        );
        return optStart.isPresent() && optEnd.isPresent() ? Optional.of(Pair.of(optStart.get(), optEnd.get())) :
                Optional.empty();
    }


    @Query("select S from TrackSnapshotRecord S " +
            "where S.targetId = :targetId and " +
            "S.timestamp = (select max(S2.timestamp) from TrackSnapshotRecord S2 " +
                "where S2.targetId = :targetId" +
            ")")
    Optional<TrackSnapshotRecord> getLatest(@Param("targetId") UUID targetId);

    @Query("select S from TrackSnapshotRecord S " +
            "where S.targetId = :targetId and " +
            "S.timestamp = (select min(S2.timestamp) from TrackSnapshotRecord S2 " +
            "where S2.targetId = :targetId" +
            ")")
    Optional<TrackSnapshotRecord> getInit(@Param("targetId") UUID targetId);

}
