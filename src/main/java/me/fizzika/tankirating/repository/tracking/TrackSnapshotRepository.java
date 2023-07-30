package me.fizzika.tankirating.repository.tracking;

import me.fizzika.tankirating.record.tracking.TrackSnapshotRecord;
import me.fizzika.tankirating.util.Pair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrackSnapshotRepository extends JpaRepository<TrackSnapshotRecord, Long> {

    Optional<TrackSnapshotRecord> findOneByTargetIdAndTimestamp(Integer targetId, LocalDateTime timestamp);

    boolean existsByTargetIdAndTimestamp(Integer targetId, LocalDateTime timestamp);

    @Query("select S.trackRecord.id from TrackSnapshotRecord S where S.timestamp between :start and :end")
    List<Long> findAllTrackIdsByTimestampBetween(LocalDateTime start, LocalDateTime end);

    Optional<TrackSnapshotRecord> findFirstByTargetIdAndTimestampBetweenOrderByTimestampAsc(Integer targetId,
                                                                                           LocalDateTime from,
                                                                                           LocalDateTime to);

    Optional<TrackSnapshotRecord> findFirstByTargetIdAndTimestampBetweenOrderByTimestampDesc(Integer targetId,
                                                                                             LocalDateTime from,
                                                                                             LocalDateTime to);

    Optional<TrackSnapshotRecord> findFirstByTargetIdAndTimestampGreaterThanEqualOrderByTimestampAsc(
            Integer targetId, LocalDateTime timestamp);

    Optional<TrackSnapshotRecord> findFirstByTargetIdAndTimestampLessThanEqualOrderByTimestampDesc(
            Integer targetId, LocalDateTime timestamp);

    default Optional<TrackSnapshotRecord> findFirstSnapshot(Integer targetId, LocalDateTime from, LocalDateTime to) {
        return findFirstByTargetIdAndTimestampBetweenOrderByTimestampAsc(targetId, from, to);
    }

    default Optional<TrackSnapshotRecord> findLastSnapshot(Integer targetId, LocalDateTime from, LocalDateTime to) {
        return findFirstByTargetIdAndTimestampBetweenOrderByTimestampDesc(targetId, from, to);
    }

    default Optional<Pair<TrackSnapshotRecord>> findBorderSnapshots(Integer targetId, LocalDateTime from,
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
            "where S.target.id = :targetId and " +
            "S.timestamp = (select max(S2.timestamp) from TrackSnapshotRecord S2 " +
                "where S2.target.id = :targetId" +
            ")")
    Optional<TrackSnapshotRecord> getLatest(@Param("targetId") Integer targetId);

    @Query("select S from TrackSnapshotRecord S " +
            "where S.target.id = :targetId and " +
            "S.timestamp = (select min(S2.timestamp) from TrackSnapshotRecord S2 " +
            "where S2.target.id = :targetId" +
            ")")
    Optional<TrackSnapshotRecord> getInit(@Param("targetId") Integer targetId);

    @Query("select S from TrackSnapshotRecord S " +
            "where S.target.id = :targetId and " +
            "DATE(S.timestamp) = :date")
    Optional<TrackSnapshotRecord> findByDate(@Param("targetId") Integer targetId,
                                             @Param("date") LocalDate date);

    @Query("select S from TrackSnapshotRecord S " +
            "where S.target.id = :targetId and " +
            "DATE(S.timestamp) between :from and :to")
    List<TrackSnapshotRecord> findAllByDate(@Param("targetId") Integer targetId,
                                            @Param("from") LocalDate from,
                                            @Param("to") LocalDate to);

    @Query(value = "select count(1) from tankirating.snapshot s where s.target_id = :targetId " +
            "and s.timestamp >= :from and s.timestamp < :to and s.has_premium is true "
            + "and s.timestamp::time = '00:00:00'", nativeQuery = true)
    int getPremiumDays(@Param("targetId") Integer targetId, @Param("from") LocalDateTime from,
                       @Param("to") LocalDateTime to);
}