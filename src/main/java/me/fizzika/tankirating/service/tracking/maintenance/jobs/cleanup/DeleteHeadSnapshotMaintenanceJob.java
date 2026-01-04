package me.fizzika.tankirating.service.tracking.maintenance.jobs.cleanup;

import static java.time.LocalDateTime.now;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.service.tracking.internal.TrackSnapshotService;
import me.fizzika.tankirating.service.tracking.maintenance.MaintenanceJob;
import org.springframework.stereotype.Component;

/**
 * Delete previous day HEAD snapshot with data for all accounts
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DeleteHeadSnapshotMaintenanceJob extends MaintenanceJob {

    private final TrackSnapshotService snapshotService;

    @Override
    public void runMaintenance() {
        LocalDateTime now = now();
        LocalDateTime headEnd = now.truncatedTo(ChronoUnit.DAYS).minusSeconds(1);
        int deleted = snapshotService.deleteHeadSnapshots(headEnd);

        log.info("Deleted {} snapshot data record", deleted);
    }

}