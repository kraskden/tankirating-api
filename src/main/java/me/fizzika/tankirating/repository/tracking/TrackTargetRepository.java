package me.fizzika.tankirating.repository.tracking;

import me.fizzika.tankirating.enums.track.TrackTargetStatus;
import me.fizzika.tankirating.enums.track.TrackTargetType;
import me.fizzika.tankirating.record.tracking.TrackTargetRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TrackTargetRepository extends JpaRepository<TrackTargetRecord, Integer> {

    Optional<TrackTargetRecord> findByNameIgnoreCaseAndType(String name, TrackTargetType type);

    boolean existsByNameIgnoreCaseAndType(String name, TrackTargetType type);

    @Query(value = "select T from TrackTargetRecord T " +
            "where (:targetType is null or T.type = :targetType) " +
            "and (:statuses is null or (T.status in :statuses)) " +
            "and (:query is null or lower(T.name) like lower(concat('%', text(:query), '%')))")
    Page<TrackTargetRecord> findAll(@Param("targetType") TrackTargetType targetType,
                                    @Param("statuses") Collection<TrackTargetStatus> statuses,
                                    @Param("query") String query,
                                    Pageable pageable);

    List<TrackTargetRecord> findAllByType(TrackTargetType type);

    @Modifying
    @Query(value = "update target t set status = 'DISABLED' " +
            "where t.status = 'FROZEN' " +
            "and exists (select 1 from \"snapshot\" s where s.\"timestamp\" > :updateDate) " +
            "and not exists " +
            "(select 1 from \"snapshot\" s where s.target_id = t.id and s.\"timestamp\" > :updateDate);", nativeQuery = true)
    int markFrozenAccountsAsBlocked(@Param("updateDate") LocalDateTime minLastUpdateDate);

    @Modifying
    @Query(value = "update TrackTargetRecord T set T.status = 'DISABLED' where T.id = :id")
    void disableAccount(@Param("id") Integer id);

}
