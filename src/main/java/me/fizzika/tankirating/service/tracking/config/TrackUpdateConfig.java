package me.fizzika.tankirating.service.tracking.config;

import static me.fizzika.tankirating.enums.track.TrackTargetStatus.ACTIVE;
import static me.fizzika.tankirating.enums.track.TrackTargetStatus.DISABLED;
import static me.fizzika.tankirating.enums.track.TrackTargetStatus.FROZEN;
import static me.fizzika.tankirating.enums.track.TrackTargetStatus.PREMIUM;
import static me.fizzika.tankirating.enums.track.TrackTargetStatus.SLEEP;

import me.fizzika.tankirating.service.tracking.update.batch.BatchTrackingUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TrackUpdateConfig {

    @Autowired
    private BatchTrackingUpdateService updateService;

    @Scheduled(cron = "${app.cron.update-premium}")
    public void updatePremium() {
        updateService.updateAccounts(PREMIUM);
    }

    @Scheduled(cron = "${app.cron.update-active}")
    public void updateActive() {
        updateService.updateAccounts(ACTIVE);
    }

    @Scheduled(cron = "${app.cron.update-frozen}")
    public void updateFrozen() {
        updateService.updateAccounts(FROZEN);
    }

    @Scheduled(cron = "${app.cron.update-sleep}")
    public void updateSleep() {
        updateService.updateAccounts(SLEEP);
    }

    @Scheduled(cron = "${app.cron.update-disabled}")
    public void updateDisabled() {
        updateService.updateAccounts(DISABLED);
    }

    @Scheduled(cron = "${app.cron.update-groups}")
    public void updateGroups() {
        updateService.updateGroups();
    }
}