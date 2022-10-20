package me.fizzika.tankirating.repository.tracking;

import me.fizzika.tankirating.dto.tracking.TrackHeatMapDTO;
import me.fizzika.tankirating.enums.PeriodUnit;
import me.fizzika.tankirating.enums.track.TrackFormat;
import me.fizzika.tankirating.model.EntityActivityTrack;
import me.fizzika.tankirating.model.date.PeriodDiffDates;
import me.fizzika.tankirating.record.tracking.TrackDiffRecord;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrackDiffRepository extends JpaRepository<TrackDiffRecord, Long> {

    //language=SQL
    String GET_ALL_DIFFS_FOR_PERIOD = "select D from TrackDiffRecord D " +
            "left join fetch D.trackRecord " +
            "where D.target.id = :targetId " +
            "and D.period = :period " +
            "and D.periodStart between :from and :to";

    Optional<TrackDiffRecord> findByTargetIdAndPeriodStartAndPeriodEnd(Integer targetId,
                                                                       LocalDateTime periodStart,
                                                                       LocalDateTime periodEnd);

    @Query(GET_ALL_DIFFS_FOR_PERIOD)
    List<TrackDiffRecord> findAllBaseDiffsForPeriod(Integer targetId, PeriodUnit period, LocalDateTime from,
                                                LocalDateTime to, Sort sort);


    @Query(GET_ALL_DIFFS_FOR_PERIOD)
    @EntityGraph(attributePaths = {"trackRecord.supplies"}, type = EntityGraph.EntityGraphType.LOAD)
    List<TrackDiffRecord> findAllFullDiffsForPeriod(Integer targetId, PeriodUnit period, LocalDateTime from,
                                                LocalDateTime to, Sort sort);

    default List<TrackDiffRecord> findAllDiffsForPeriod(Integer targetId, PeriodUnit period, LocalDateTime from,
                                                        LocalDateTime to, Sort sort, TrackFormat format) {
        // Static typing is a sh~
        return format == TrackFormat.FULL ?
                findAllFullDiffsForPeriod(targetId, period, from, to, sort) :
                findAllBaseDiffsForPeriod(targetId, period, from, to, sort);
    }

    @Query(value = "select D from TrackDiffRecord D " +
            "left join fetch D.trackRecord " +
            "where D.target.id = :targetId " +
            "and D.period = :period " +
            "and D.periodStart = :periodStart")
    Optional<TrackDiffRecord> findDiffForPeriod(Integer targetId, PeriodUnit period,
                                                LocalDateTime periodStart);

    @Query("select D from TrackDiffRecord D left join fetch D.trackRecord " +
            "where D.target.id = :targetId and D.period = 'ALL_TIME'")
    Optional<TrackDiffRecord> findAllTimeDiff(Integer targetId);


    @Query("select new me.fizzika.tankirating.dto.tracking.TrackHeatMapDTO(D.periodStart, T.time, T.gold, D.premiumDays) from TrackDiffRecord D " +
            " left join fetch TrackRecord T on D.trackRecord = T " +
            " where D.target.id = :targetId and D.period = 'DAY' and " +
            " D.periodStart between :from and :to")
    List<TrackHeatMapDTO> getHeatMap(@Param("targetId") Integer targetId,
                                     @Param("from") LocalDateTime from,
                                     @Param("to") LocalDateTime to,
                                     Sort sort);


    @Query("select new me.fizzika.tankirating.model.EntityActivityTrack(A.entityId, sum(A.time), sum(A.score)) " +
            "from TrackDiffRecord D " +
            "inner join fetch TrackRecord T on D.trackRecord = T " +
            "left join fetch TrackActivityRecord A on A.track = T " +
            "where D.periodStart = :periodStart and D.period = :period " +
            "and (:minScore is null or D.maxScore >= :minScore) " +
            "and (:maxScore is null or D.maxScore <= :maxScore) " +
            "group by A.entityId")
    List<EntityActivityTrack> getActivityStat(@Param("period") PeriodUnit period,
                                              @Param("periodStart") LocalDateTime periodStart,
                                              @Param("minScore") Integer minScore,
                                              @Param("maxScore") Integer maxScore);


    @Query("select new me.fizzika.tankirating.model.date.PeriodDiffDates(D.periodStart, D.periodEnd, min(D.trackStart), max(D.trackEnd)) " +
            "from TrackDiffRecord D " +
            "where D.period = :period " +
            "group by D.periodStart, D.periodEnd " +
            "order by D.periodStart asc")
    List<PeriodDiffDates> getAllPeriodDates(PeriodUnit period);

}
