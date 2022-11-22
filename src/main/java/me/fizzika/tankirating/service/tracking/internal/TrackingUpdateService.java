package me.fizzika.tankirating.service.tracking.internal;

import me.fizzika.tankirating.dto.TrackTargetDTO;

public interface TrackingUpdateService {

    void updateOne(TrackTargetDTO account);

    void updateAll();

}
