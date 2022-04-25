package me.fizzika.tankirating.repository;

import me.fizzika.tankirating.record.tracking.TrackTargetRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TrackTargetRepository extends JpaRepository<TrackTargetRecord, UUID> {

}
