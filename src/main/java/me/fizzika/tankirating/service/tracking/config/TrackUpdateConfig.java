package me.fizzika.tankirating.service.tracking.config;

import static me.fizzika.tankirating.enums.track.TrackTargetStatus.ACTIVE;
import static me.fizzika.tankirating.enums.track.TrackTargetStatus.DISABLED;
import static me.fizzika.tankirating.enums.track.TrackTargetStatus.FROZEN;
import static me.fizzika.tankirating.enums.track.TrackTargetStatus.PREMIUM;
import static me.fizzika.tankirating.enums.track.TrackTargetStatus.SLEEP;

import javax.annotation.Resource;
import me.fizzika.tankirating.service.tracking.internal.TrackingUpdateService;
import me.fizzika.tankirating.service.tracking.sanitizer.impl.SleepAccountsSanitizer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TrackUpdateConfig {

    @Resource
    private TrackingUpdateService updateService;
    @Resource
    private SleepAccountsSanitizer sleepSanitizer;

    @Scheduled(cron = "${app.cron.update-premium}")
    public void updatePremium() {
        updateService.updateAll(PREMIUM);
    }

    @Scheduled(cron = "${app.cron.update-active}")
    public void updateActive() {
        updateService.updateAll(ACTIVE);
    }

    @Scheduled(cron = "${app.cron.update-frozen}")
    public void updateFrozen() {
        updateService.updateAll(FROZEN);
    }

    @Scheduled(cron = "${app.cron.update-sleep}")
    public void updateSleep() {
        updateService.updateAll(SLEEP);
        sleepSanitizer.sanitize();
    }

    @Scheduled(cron = "${app.cron.update-disabled}")
    public void updateDisabled() {
        updateService.updateAll(DISABLED);
    }
}