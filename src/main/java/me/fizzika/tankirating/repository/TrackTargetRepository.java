package me.fizzika.tankirating.repository;

import me.fizzika.tankirating.record.tracking.TrackTargetRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface TrackTargetRepository extends JpaRepository<TrackTargetRecord, UUID> {

    Optional<TrackTargetRecord> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    @Query(value = "select * from target t where not starts_with(t.name, '~')", nativeQuery = true)
    Page<TrackTargetRecord> findAllAccounts(Pageable pageable);

}
