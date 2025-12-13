package me.fizzika.tankirating.repository.tracking;


import java.time.LocalDateTime;
import me.fizzika.tankirating.enums.DiffPeriodUnit;
import me.fizzika.tankirating.record.tracking.TrackActivityRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackActivityRepository extends JpaRepository<TrackActivityRecord, Long> {

    @Modifying
    @Query("""
            DELETE FROM TrackActivityRecord TA
            WHERE TA.track.id IN (
                SELECT DISTINCT D.trackRecord.id
                FROM TrackDiffRecord D
                WHERE D.period = :period
                  AND D.periodStart < :beforeAt
            )
            """)
    int deleteDiffActivities(DiffPeriodUnit period, LocalDateTime beforeAt);

}