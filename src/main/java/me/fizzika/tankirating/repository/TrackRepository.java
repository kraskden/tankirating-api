package me.fizzika.tankirating.repository;

import me.fizzika.tankirating.record.tracking.TrackRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TrackRepository extends JpaRepository<TrackRecord, UUID> {

}
