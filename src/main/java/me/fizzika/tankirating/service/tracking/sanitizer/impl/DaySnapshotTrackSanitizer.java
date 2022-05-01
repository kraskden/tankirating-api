package me.fizzika.tankirating.service.tracking.sanitizer.impl;

import me.fizzika.tankirating.service.tracking.sanitizer.TrackSanitizer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DaySnapshotTrackSanitizer implements TrackSanitizer {

//    @Scheduled(cron = "")
    @Override
    public void sanitize() {
        // delete old track snapshots
    }

}
