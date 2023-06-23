package me.fizzika.tankirating.service.tracking.config;

import me.fizzika.tankirating.enums.track.TrackTargetStatus;
import me.fizzika.tankirating.service.tracking.internal.TrackingUpdateService;
import me.fizzika.tankirating.service.tracking.sanitizer.impl.SleepAccountsSanitizer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class TrackUpdateConfig {

    @Resource
    private TrackingUpdateService updateService;
    @Resource
    private SleepAccountsSanitizer sleepSanitizer;

    @Scheduled(cron = "${app.cron.update-active}")
    public void updateActive() {
        updateService.updateAll(TrackTargetStatus.ACTIVE);
    }

    @Scheduled(cron = "${app.cron.update-frozen}")
    public void updateFrozen() {
        updateService.updateAll(TrackTargetStatus.FROZEN);
    }

    @Scheduled(cron = "${app.cron.update-sleep}")
    public void updateSleep() {
        updateService.updateAll(TrackTargetStatus.SLEEP);
        sleepSanitizer.sanitize();
    }
}
