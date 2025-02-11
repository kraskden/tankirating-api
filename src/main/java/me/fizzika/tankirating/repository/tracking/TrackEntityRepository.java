package me.fizzika.tankirating.repository.tracking;

import me.fizzika.tankirating.record.tracking.TrackEntityRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackEntityRepository extends JpaRepository<TrackEntityRecord, Short> {
}