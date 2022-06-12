package me.fizzika.tankirating.repository.online;

import me.fizzika.tankirating.record.online.OnlineSnapshotRecord;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OnlineSnapshotRepository extends JpaRepository<OnlineSnapshotRecord, LocalDateTime> {

    @Query("from OnlineSnapshotRecord R where " +
            "(cast(:from as timestamp) is null or R.timestamp >= :from) " +
            "and (cast(:to as timestamp) is null or R.timestamp <= :to)")
    List<OnlineSnapshotRecord> findAllInRange(@Nullable @Param("from") LocalDateTime from,
                                              @Nullable @Param("to") LocalDateTime to, Sort sort);

}
