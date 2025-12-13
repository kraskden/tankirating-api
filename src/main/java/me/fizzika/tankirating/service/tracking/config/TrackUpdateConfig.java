package me.fizzika.tankirating.service.tracking.config;

import static me.fizzika.tankirating.enums.track.TrackTargetStatus.ACTIVE;
import static me.fizzika.tankirating.enums.track.TrackTargetStatus.DISABLED;
import static me.fizzika.tankirating.enums.track.TrackTargetStatus.FROZEN;
import static me.fizzika.tankirating.enums.track.TrackTargetStatus.PREMIUM;
import static me.fizzika.tankirating.enums.track.TrackTargetStatus.SLEEP;

import jakarta.annotation.Resource;
import me.fizzika.tankirating.service.tracking.internal.TrackingUpdateService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TrackUpdateConfig {

    @Resource
    private TrackingUpdateService updateService;

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
    }

    @Scheduled(cron = "${app.cron.update-disabled}")
    public void updateDisabled() {
        updateService.updateAll(DISABLED);
    }
}