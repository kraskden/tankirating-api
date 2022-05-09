package me.fizzika.tankirating.repository;

import me.fizzika.tankirating.record.tracking.TrackTargetRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TrackTargetRepository extends JpaRepository<TrackTargetRecord, Integer> {

    Optional<TrackTargetRecord> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    @Query(value = "select * from target t where not starts_with(t.name, '~')", nativeQuery = true)
    Page<TrackTargetRecord> findAllAccounts(Pageable pageable);

}
