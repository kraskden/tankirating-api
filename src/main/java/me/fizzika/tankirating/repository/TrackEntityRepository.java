package me.fizzika.tankirating.repository;

import me.fizzika.tankirating.record.tracking.TrackEntityRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackEntityRepository extends CrudRepository<TrackEntityRecord, Short> {
}
