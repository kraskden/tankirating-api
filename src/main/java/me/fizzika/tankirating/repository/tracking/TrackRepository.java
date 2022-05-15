package me.fizzika.tankirating.repository.tracking;

import me.fizzika.tankirating.record.tracking.TrackRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackRepository extends JpaRepository<TrackRecord, Long> {

}
