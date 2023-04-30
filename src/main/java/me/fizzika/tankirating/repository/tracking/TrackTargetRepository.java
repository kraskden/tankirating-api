package me.fizzika.tankirating.repository.tracking;

import me.fizzika.tankirating.enums.track.TrackTargetType;
import me.fizzika.tankirating.record.tracking.TrackTargetRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TrackTargetRepository extends JpaRepository<TrackTargetRecord, Integer>,
        JpaSpecificationExecutor<TrackTargetRecord> {

    Optional<TrackTargetRecord> findByNameIgnoreCaseAndType(String name, TrackTargetType type);
    Optional<TrackTargetRecord> findByIdAndType(Integer id, TrackTargetType type);

    boolean existsByNameIgnoreCaseAndType(String name, TrackTargetType type);

    List<TrackTargetRecord> findAllByType(TrackTargetType type);

    @Modifying
    @Query(value = "update target t set status = 'DISABLED' " +
            "where t.status = 'FROZEN' " +
            "and exists (select 1 from \"snapshot\" s where s.\"timestamp\" > :updateDate) " +
            "and not exists " +
            "(select 1 from \"snapshot\" s where s.target_id = t.id and s.\"timestamp\" > :updateDate);", nativeQuery = true)
    void markFrozenAccountsAsBlocked(@Param("updateDate") LocalDateTime minLastUpdateDate);

}
