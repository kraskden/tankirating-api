package me.fizzika.tankirating.service.tracking.internal;

import me.fizzika.tankirating.dto.target.TrackTargetDTO;
import me.fizzika.tankirating.model.AccountUpdateResult;

public interface TrackingUpdateService {

    AccountUpdateResult updateOne(TrackTargetDTO account);

    void updateAllActive();

    void updateAllFrozenAndSleep();
}
