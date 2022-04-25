package me.fizzika.tankirating.repository;

import me.fizzika.tankirating.record.tracking.TrackSnapshotRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TrackSnapshotRepository extends JpaRepository<TrackSnapshotRecord, UUID> {
}
