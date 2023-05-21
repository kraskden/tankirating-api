package me.fizzika.tankirating.service.tracking.sanitizer.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.service.tracking.internal.TrackSnapshotService;
import me.fizzika.tankirating.service.tracking.sanitizer.TrackSanitizer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static java.time.LocalDateTime.now;

/**
 * Delete previous day HEAD snapshot with data for all accounts
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class HeadSnapshotSanitizer implements TrackSanitizer {

    private final TrackSnapshotService snapshotService;

    @Override
    @Transactional
    @Scheduled(cron = "${app.cron.head-snapshot-sanitizer}")
    public void sanitize() {
        log.info("Start {} sanitizer", this.getClass().getSimpleName());

        LocalDateTime now = now();
        LocalDateTime headStart = now.minusDays(1).truncatedTo(ChronoUnit.DAYS).plusSeconds(1);
        LocalDateTime headEnd = now.truncatedTo(ChronoUnit.DAYS).minusSeconds(1);
        int deleted = snapshotService.deleteAllInRangeWithTrackData(headStart, headEnd);

        log.info("Finish {} sanitizer, deleted {} snapshot data record", this.getClass().getSimpleName(),
                deleted);
    }

}
