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
import me.fizzika.tankirating.record.tracking.TrackTargetRecord;
import me.fizzika.tankirating.repository.TrackDiffRepository;
import me.fizzika.tankirating.repository.TrackRepository;
import me.fizzika.tankirating.service.tracking.AlternativaTrackingService;
import me.fizzika.tankirating.service.tracking.TrackSnapshotService;
import me.fizzika.tankirating.service.tracking.TrackTargetService;
import me.fizzika.tankirating.service.tracking.TrackingUpdateService;
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
    
    private final TrackSnapshotService snapshotService;
    private final TrackTargetService targetService;
    private final TrackDataMapper dataMapper;

    private final TrackDiffRepository diffRepository;
    private final TrackRepository trackRepository;

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
                .thenAccept(data -> updateAccountData(targetId, data))
                .whenComplete((res, ex) -> {
                    if (ex == null) {
                        log.info("Updated {}", nickname);
                    } else {
                        log.error("Error due updating {}", nickname, ex);
                    }
                });
    }

    private void updateAccountData(Integer targetId, TrackFullData currentData) {
        LocalDateTime now = LocalDateTime.now();

        // Save current snapshot
        saveDaySnapshot(targetId, now, currentData);

        // Calculate diff for each period
        for (TrackDiffPeriod diffPeriod : TrackDiffPeriod.values()) {
            DatePeriod diffDates = diffPeriod.getDatePeriod(now);

            Optional<TrackFullData> periodDiff = snapshotService.findClosestSnapshot(targetId, diffDates.getStart(), diffDates.getEnd())
                    .map(TrackSnapshot::getTrackData)
                    .map(snap -> TrackData.diff(currentData, snap))
                    .filter(TrackFullData::notEmpty);

            TrackDiffRecord diffRecord = diffRepository.findByTargetIdAndPeriodStartAndPeriodEnd(targetId,
                    diffDates.getStart(), diffDates.getEnd()).orElseGet(() -> emptyRecord(targetId, diffPeriod, diffDates));
            diffRecord.setTrackEnd(now);
            diffRecord.setTrackStart(diffRecord.getTrackStart() != null ? diffRecord.getTrackStart() : now);
            if (diffRecord.getTrackRecord() != null) {
                trackRepository.delete(diffRecord.getTrackRecord());
            }
            diffRecord.setTrackRecord(periodDiff.map(dataMapper::toTrackRecord).orElse(null));
            diffRepository.save(diffRecord);
        }

    }


    private void saveDaySnapshot(Integer targetId, LocalDateTime now, TrackFullData data) {
        LocalDateTime dayStart = now.truncatedTo(ChronoUnit.DAYS);
        if (snapshotService.existsSnapshot(targetId, dayStart)) {
            snapshotService.saveSnapshot(new TrackSnapshot(targetId, now, data));
        } else {
            snapshotService.saveSnapshot(new TrackSnapshot(targetId, dayStart, data));
        }
    }

    private TrackDiffRecord emptyRecord(Integer targetId, TrackDiffPeriod period, DatePeriod periodDates) {
        var rec = new TrackDiffRecord();
        var target = new TrackTargetRecord();
        target.setId(targetId);

        rec.setTarget(target);
        rec.setPeriodStart(periodDates.getStart());
        rec.setPeriodEnd(periodDates.getEnd());
        rec.setPeriod(period);
        return rec;
    }



}
