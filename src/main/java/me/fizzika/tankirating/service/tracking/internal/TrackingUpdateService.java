package me.fizzika.tankirating.service.tracking.internal;

import me.fizzika.tankirating.dto.target.TrackTargetDTO;
import me.fizzika.tankirating.model.AccountUpdateResult;
import org.springframework.scheduling.annotation.Scheduled;

public interface TrackingUpdateService {

    AccountUpdateResult updateOne(TrackTargetDTO account);

    void updateAllActiveAndFrozen();

    void updateAllFrozenAndSleep();

    @Scheduled(cron = "${app.cron.update-frozen}")
    void updateFrozen();
}
