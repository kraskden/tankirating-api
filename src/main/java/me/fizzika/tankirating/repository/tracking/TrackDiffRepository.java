package me.fizzika.tankirating.repository.tracking;

import me.fizzika.tankirating.enums.PeriodUnit;
import me.fizzika.tankirating.record.tracking.TrackDiffRecord;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrackDiffRepository extends JpaRepository<TrackDiffRecord, Long> {

    Optional<TrackDiffRecord> findByTargetIdAndPeriodStartAndPeriodEnd(Integer targetId,
                                                                       LocalDateTime periodStart,
                                                                       LocalDateTime periodEnd);

    @Query("select D from TrackDiffRecord D " +
            "where D.target.id = :targetId " +
            "and D.period = :period " +
            "and D.periodStart between :from and :to")
    List<TrackDiffRecord> findAllDiffsForPeriod(Integer targetId, PeriodUnit period, LocalDateTime from,
                                                LocalDateTime to, Sort sort);



    @Query(value = "select D from TrackDiffRecord D " +
            "where D.target.id = :targetId " +
            "and D.period = :period " +
            "and D.periodStart = :periodStart")
    Optional<TrackDiffRecord> findDiffForPeriod(Integer targetId, PeriodUnit period,
                                                LocalDateTime periodStart);

    @Query("select D from TrackDiffRecord D where D.target.id = :targetId and D.period = 'ALL_TIME'")
    Optional<TrackDiffRecord> findAllTimeDiff(Integer targetId);

}