package me.fizzika.tankirating.service.tracking.internal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.enums.track.TrackTargetType;
import me.fizzika.tankirating.mapper.AlternativaTrackingMapper;
import me.fizzika.tankirating.service.tracking.*;
import me.fizzika.tankirating.service.tracking.internal.AlternativaTrackingService;
import me.fizzika.tankirating.service.tracking.internal.TrackStoreService;
import me.fizzika.tankirating.service.tracking.internal.TrackingUpdateService;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackingUpdateServiceImpl implements TrackingUpdateService {

    private final AlternativaTrackingService alternativaService;
    private final AlternativaTrackingMapper alternativaMapper;
    
    private final TrackTargetService targetService;
    private final TrackStoreService trackStoreService;

    @Override
    public void updateAccount(Integer targetId, String nickname) {
        updateAccountAsync(targetId, nickname);
    }

    @Override
    public void updateAll() {
        targetService.getAllTargets().stream()
                .filter(t -> t.getType() == TrackTargetType.ACCOUNT && t.getStatus().isUpdatable())
                .forEach(t -> updateAccount(t.getId(), t.getName()));
    }

    private CompletableFuture<Void> updateAccountAsync(Integer targetId, String nickname) {
        return alternativaService.getTracking(nickname)
                .thenApply(alternativaMapper::toFullTrackModelWithPremium)
                .thenAccept(data -> trackStoreService.updateTargetData(targetId, data.getTrackData(), data.isHasPremium()))
                .whenComplete((res, ex) -> {
                    if (ex == null) {
                        log.info("Updated {}", nickname);
                    } else {
                        log.error("Error due updating {}", nickname, ex);
                    }
                });
    }

}
