package me.fizzika.tankirating.repository;

import me.fizzika.tankirating.record.tracking.TrackDiffRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrackDiffRepository extends JpaRepository<TrackDiffRecord, UUID> {

    Optional<TrackDiffRecord> findByTargetIdAndPeriodStartAndPeriodEnd(UUID targetId,
                                                                       LocalDateTime periodStart,
                                                                       LocalDateTime periodEnd);

}
