package me.fizzika.tankirating.service.tracking.internal;

import me.fizzika.tankirating.dto.target.TrackTargetDTO;
import me.fizzika.tankirating.enums.track.TrackTargetStatus;
import me.fizzika.tankirating.model.AccountUpdateResult;

public interface TrackingUpdateService {

    AccountUpdateResult updateOne(TrackTargetDTO account);
    void updateAll(TrackTargetStatus targetStatus);
}
