package me.fizzika.tankirating.repository.tracking;

import java.time.LocalDateTime;
import me.fizzika.tankirating.enums.SnapshotPeriod;
import me.fizzika.tankirating.record.tracking.TrackRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TrackRepository extends JpaRepository<TrackRecord, Long> {

    @Query(value = """
            delete from TrackRecord where id in (
                select s.trackRecord.id from TrackSnapshotRecord s
                where s.period = :period
                and s.timestamp < :beforeAt
                and not exists (
                    select 1 from TrackSnapshotRecord s2
                        where s2.period > s.period and s2.trackRecord.id=s.trackRecord.id
                )
            )
            """)
    @Modifying
    int deleteSnapshotTracks(SnapshotPeriod period, LocalDateTime beforeAt);
}