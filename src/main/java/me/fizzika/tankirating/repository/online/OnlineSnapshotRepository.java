package me.fizzika.tankirating.repository.online;

import me.fizzika.tankirating.record.online.OnlineSnapshotRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface OnlineSnapshotRepository extends JpaRepository<OnlineSnapshotRecord, LocalDateTime> {
}
