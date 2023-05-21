package me.fizzika.tankirating.repository.tracking;

import me.fizzika.tankirating.dto.rating.AccountRatingDTO;
import me.fizzika.tankirating.enums.PeriodUnit;
import me.fizzika.tankirating.enums.track.TrackTargetStatus;
import me.fizzika.tankirating.enums.track.TrackTargetType;
import me.fizzika.tankirating.record.tracking.TrackTargetRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TrackTargetRepository extends JpaRepository<TrackTargetRecord, Integer>,
        JpaSpecificationExecutor<TrackTargetRecord> {

    Optional<TrackTargetRecord> findByNameIgnoreCaseAndType(String name, TrackTargetType type);
    Optional<TrackTargetRecord> findByIdAndType(Integer id, TrackTargetType type);

    boolean existsByNameIgnoreCaseAndType(String name, TrackTargetType type);

    List<TrackTargetRecord> findAllByType(TrackTargetType type);

    @Query("from TrackTargetRecord R where R.type = 'ACCOUNT' and lower(R.name) in :nicknames")
    List<TrackTargetRecord> findAllAccountsByNicknamesInLowerCase(Collection<String> nicknames);

    @Modifying
    @Query(value = "update target t set status = 'DISABLED' " +
            "where t.status = 'FROZEN' " +
            "and exists (select 1 from \"snapshot\" s where s.\"timestamp\" > :updateDate) " +
            "and not exists " +
            "(select 1 from \"snapshot\" s where s.target_id = t.id and s.\"timestamp\" > :updateDate);", nativeQuery = true)
    void markFrozenAccountsAsBlocked(@Param("updateDate") LocalDateTime minLastUpdateDate);

    @Modifying
    @Query(value = "update target t set status = 'SLEEP' " +
            "where t.status = 'ACTIVE' " +
            "and t.type = 'ACCOUNT' " +
            "and not exists(select 1 from \"diff\" d " +
                "where d.period_start >= :minActivityDate " +
                "and d.period = 'DAY' " +
                "and d.target_id = t.id " +
                "and d.track_id is not null) ",
            nativeQuery = true)
    void markActiveAccountsAsSleep(LocalDateTime minActivityDate);

    int countByStatus(TrackTargetStatus status);

    @Query("select new me.fizzika.tankirating.dto.rating.AccountRatingDTO( " +
            "T.id, T.name, D.maxScore, TR.time, TR.kills, TR.deaths, TR.score, TR.cry) " +
            "from TrackDiffRecord D " +
            "left join D.trackRecord TR " +
            "left join D.target T " +
            "where T.status <> 'DISABLED' and D.period = :period and D.periodStart = :periodStart " +
            "and T.type = 'ACCOUNT' " +
            "and (:minScore is null or D.maxScore >= :minScore)")
    Page<AccountRatingDTO> getAccountRating(PeriodUnit period, LocalDateTime periodStart,
                                            Integer minScore,
                                            Pageable pageable);


    @Query("select lower(T.name) from TrackTargetRecord T where T.type = 'ACCOUNT' " +
            "and lower(T.name) in :queriedNicknames ")
    Set<String> existingNicknamesInLowerCase(Collection<String> queriedNicknames);

    @Query("select count(distinct D.target.id) from TrackDiffRecord D " +
            "where D.period = 'DAY' " +
            "and (:maxScore is null or D.maxScore < :maxScore) " +
            "and (:minScore is null or D.maxScore > :minScore) " +
            "and (cast(:from as date) is null or D.periodStart > :from) " +
            "and (cast(:to as date) is null or D.periodStart < :to) " +
            "and D.trackRecord is not null")
    long getPlayedCount(Integer minScore, Integer maxScore, LocalDateTime from, LocalDateTime to);
}
