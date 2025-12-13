package me.fizzika.tankirating.repository.tracking;

import java.time.LocalDateTime;
import me.fizzika.tankirating.enums.DiffPeriodUnit;
import me.fizzika.tankirating.record.tracking.TrackUsageRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackUsageRepository extends JpaRepository<TrackUsageRecord, Long> {

    @Modifying
    @Query("""
            DELETE FROM TrackUsageRecord TU
            WHERE TU.track.id IN (
                SELECT DISTINCT D.trackRecord.id
                FROM TrackDiffRecord D
                WHERE D.period = :period
                  AND D.periodStart < :beforeAt
            )
            """)
    int deleteDiffUsages(DiffPeriodUnit period, LocalDateTime beforeAt);
}