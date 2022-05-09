package me.fizzika.tankirating.repository;

import me.fizzika.tankirating.enums.track.TrackTargetType;
import me.fizzika.tankirating.record.tracking.TrackTargetRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TrackTargetRepository extends JpaRepository<TrackTargetRecord, Integer> {

    Optional<TrackTargetRecord> findByNameIgnoreCaseAndType(String name, TrackTargetType type);

    boolean existsByNameIgnoreCaseAndType(String name, TrackTargetType type);

    @Query(value = "select T from TrackTargetRecord T where T.type = 'ACCOUNT'")
    Page<TrackTargetRecord> findAllAccounts(Pageable pageable);

}
