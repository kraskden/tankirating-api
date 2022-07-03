package me.fizzika.tankirating.repository.tracking;

import me.fizzika.tankirating.enums.track.TrackTargetType;
import me.fizzika.tankirating.record.tracking.TrackTargetRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrackTargetRepository extends JpaRepository<TrackTargetRecord, Integer> {

    Optional<TrackTargetRecord> findByNameIgnoreCaseAndType(String name, TrackTargetType type);

    boolean existsByNameIgnoreCaseAndType(String name, TrackTargetType type);

    @Query(value = "select T from TrackTargetRecord T " +
            "where (:targetType is null or T.type = :targetType) " +
            "and (:query is null or lower(T.name) like lower(concat('%', text(:query), '%')))")
    Page<TrackTargetRecord> findAll(@Param("targetType") TrackTargetType targetType,
                                            @Param("query") String query,
                                            Pageable pageable);

    List<TrackTargetRecord> findAllByType(TrackTargetType type);

}
