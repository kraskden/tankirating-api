package me.fizzika.tankirating.service.tracking.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.enums.track.TrackDiffPeriod;
import me.fizzika.tankirating.enums.track.TrackTargetType;
import me.fizzika.tankirating.mapper.AlternativaTrackingMapper;
import me.fizzika.tankirating.mapper.TrackDataMapper;
import me.fizzika.tankirating.model.DatePeriod;
import me.fizzika.tankirating.model.TrackData;
import me.fizzika.tankirating.model.TrackSnapshot;
import me.fizzika.tankirating.model.track_data.TrackFullData;
import me.fizzika.tankirating.record.tracking.TrackDiffRecord;
import me.fizzika.tankirating.record.tracking.TrackSnapshotRecord;
import me.fizzika.tankirating.record.tracking.TrackTargetRecord;
import me.fizzika.tankirating.repository.TrackDiffRepository;
import me.fizzika.tankirating.repository.TrackRepository;
import me.fizzika.tankirating.repository.TrackSnapshotRepository;
import me.fizzika.tankirating.service.tracking.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackingUpdateServiceImpl implements TrackingUpdateService {

    private final AlternativaTrackingService alternativaService;
    private final AlternativaTrackingMapper alternativaMapper;
    
    private final TrackTargetService targetService;
    private final TargetTrackingService targetTrackingService;

    @Override
    public void updateAccount(Integer targetId, String nickname) {
        updateAccountAsync(targetId, nickname);
    }

    @Override
    public void updateAll() {
        targetService.getAllTargets().stream()
                .filter(t -> t.getType() == TrackTargetType.ACCOUNT)
                .forEach(t -> updateAccount(t.getId(), t.getName()));
    }

    private CompletableFuture<Void> updateAccountAsync(Integer targetId, String nickname) {
        return alternativaService.getTracking(nickname)
                .thenApply(alternativaMapper::toFullTrackModel)
                .thenAccept(data -> targetTrackingService.updateTargetData(targetId, data))
                .whenComplete((res, ex) -> {
                    if (ex == null) {
                        log.info("Updated {}", nickname);
                    } else {
                        log.error("Error due updating {}", nickname, ex);
                    }
                });
    }

}
