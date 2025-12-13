package me.fizzika.tankirating.repository.tracking;

import java.util.Set;
import me.fizzika.tankirating.enums.SnapshotPeriod;
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

    @Query("from TrackSnapshotRecord s where s.target.id = :targetId and s.timestamp = :timestamp and s.trackRecord is not null")
    Optional<TrackSnapshotRecord> findByTargetAndTimestamp(Integer targetId, LocalDateTime timestamp);

    boolean existsByTargetIdAndTimestampAndTrackRecordNotNull(Integer targetId, LocalDateTime timestamp);

    @Query("select S.trackRecord.id from TrackSnapshotRecord S where S.timestamp between :start and :end "
            + "and S.trackRecord is not null")
    List<Long> findAllTrackIdsByTimestampBetween(LocalDateTime start, LocalDateTime end);

    @Query("from TrackSnapshotRecord S where S.target.id = :targetId and S.timestamp between :from and :to and S.trackRecord is not null"
            + " order by S.timestamp asc limit 1")
    Optional<TrackSnapshotRecord> findFirstSnapshotInPeriod(Integer targetId,
                                                            LocalDateTime from,
                                                            LocalDateTime to);

    @Query("from TrackSnapshotRecord S where S.target.id = :targetId and S.timestamp between :from and :to and S.trackRecord is not null"
            + " order by S.timestamp desc limit 1")
    Optional<TrackSnapshotRecord> findLastSnapshotInPeriod(Integer targetId,
                                                           LocalDateTime from,
                                                           LocalDateTime to);

    @Query("from TrackSnapshotRecord S where S.trackRecord is not null and S.target.id = :targetId and S.timestamp >= :from order by S.timestamp asc limit 1")
    Optional<TrackSnapshotRecord> findClosestSnapshotAfterTimestamp(Integer targetId, LocalDateTime from);

    @Query("from TrackSnapshotRecord S where S.trackRecord is not null and S.target.id = :targetId and S.timestamp <= :to order by S.timestamp desc limit 1")
    Optional<TrackSnapshotRecord> findClosestSnapshotBeforeTimestamp(
            Integer targetId, LocalDateTime to);

    default Optional<Pair<TrackSnapshotRecord>> findBorderSnapshots(Integer targetId, LocalDateTime from,
                                                                    LocalDateTime to) {
        Optional<TrackSnapshotRecord> optStart = findClosestSnapshotAfterTimestamp(
                targetId, from
        );
        Optional<TrackSnapshotRecord> optEnd = findClosestSnapshotBeforeTimestamp(
                targetId, to
        );
        return optStart.isPresent() && optEnd.isPresent() ? Optional.of(Pair.of(optStart.get(), optEnd.get())) :
                Optional.empty();
    }

    @Query("select S from TrackSnapshotRecord S " +
            "where S.target.id = :targetId and S.trackRecord is not null and " +
            "S.timestamp = (select max(S2.timestamp) from TrackSnapshotRecord S2 " +
            "where S2.target.id = :targetId" +
            ")")
    Optional<TrackSnapshotRecord> getLatest(@Param("targetId") Integer targetId);

    @Query("select S from TrackSnapshotRecord S where S.period = 'INIT'")
    Optional<TrackSnapshotRecord> getInit(@Param("targetId") Integer targetId);

    @Query("select S from TrackSnapshotRecord S " +
            "where S.trackRecord is not null and S.target.id = :targetId and " +
            "DATE(S.timestamp) = :date")
    Optional<TrackSnapshotRecord> findByDate(@Param("targetId") Integer targetId,
                                             @Param("date") LocalDate date);

    @Query("select S from TrackSnapshotRecord S " +
            "where S.trackRecord is not null and S.target.id = :targetId and " +
            "DATE(S.timestamp) between :from and :to")
    List<TrackSnapshotRecord> findAllByDate(@Param("targetId") Integer targetId,
                                            @Param("from") LocalDate from,
                                            @Param("to") LocalDate to);

    @Query(value = """
            select s.period from "snapshot" s
            	where s.target_id = :targetId and
            	(
            		(s."period" = 'INIT') or
            		(s.period = 'YEAR' and s.timestamp >= date_trunc('year', CAST(:now as timestamp))) or
            		(s."period" = 'MONTH' and s.timestamp >= date_trunc('month', CAST(:now as timestamp))) or
            		(s."period" = 'WEEK' and s.timestamp >= date_trunc('week', CAST(:now as timestamp)))
            	)
            """, nativeQuery = true)
    Set<SnapshotPeriod> findCurrentPeriods(Integer targetId, LocalDateTime now);

    @Query(value = "select count(1) from tankirating.snapshot s where s.target_id = :targetId " +
            "and s.timestamp >= :from and s.timestamp < :to and s.has_premium is true "
            + "and cast(s.timestamp as time) = '00:00:00'", nativeQuery = true)
    int getPremiumDays(@Param("targetId") Integer targetId, @Param("from") LocalDateTime from,
                       @Param("to") LocalDateTime to);
}