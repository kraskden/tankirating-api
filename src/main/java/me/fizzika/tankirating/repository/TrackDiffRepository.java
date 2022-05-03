package me.fizzika.tankirating.repository;

import me.fizzika.tankirating.enums.track.TrackDiffPeriod;
import me.fizzika.tankirating.record.tracking.TrackDiffRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrackDiffRepository extends JpaRepository<TrackDiffRecord, UUID> {

    Optional<TrackDiffRecord> findByTargetIdAndPeriodStartAndPeriodEnd(UUID targetId,
                                                                       LocalDateTime periodStart,
                                                                       LocalDateTime periodEnd);

    @Query("select D from TrackDiffRecord D " +
            "where D.target.id = :targetId " +
            "and D.period = :period " +
            "and D.periodStart between :from and :to")
    List<TrackDiffRecord> findDiffs(UUID targetId, TrackDiffPeriod period, LocalDateTime from,
                                    LocalDateTime to);

    @Query("select D from TrackDiffRecord D where D.target.id = :targetId and D.period = 'ALL_TIME'")
    Optional<TrackDiffRecord> findAllTimeDiff(UUID targetId);

}
