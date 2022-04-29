package me.fizzika.tankirating.service.tracking.impl;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.enums.TrackDiffPeriod;
import me.fizzika.tankirating.enums.TrackTargetType;
import me.fizzika.tankirating.mapper.AlternativaTrackingMapper;
import me.fizzika.tankirating.mapper.TrackDataMapper;
import me.fizzika.tankirating.model.DatePeriod;
import me.fizzika.tankirating.model.TrackData;
import me.fizzika.tankirating.model.TrackSnapshot;
import me.fizzika.tankirating.model.track_data.TrackFullData;
import me.fizzika.tankirating.record.tracking.TrackDiffRecord;
import me.fizzika.tankirating.repository.TrackDiffRepository;
import me.fizzika.tankirating.repository.TrackRepository;
import me.fizzika.tankirating.service.tracking.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

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
    @Transactional
    public void updateAccount(UUID targetId, String nickname) {
        TrackFullData currentData = alternativaMapper.toFullTrackModel(alternativaService.getTracking(nickname));
        LocalDateTime now = LocalDateTime.now();

        // Save current snapshot
        snapshotService.saveSnapshot(new TrackSnapshot(targetId, now, currentData));

        // Calculate diff for each period
        for (TrackDiffPeriod diffPeriod : TrackDiffPeriod.values()) {
            DatePeriod diffDates = diffPeriod.getDatePeriod(now);

            TrackFullData periodDiff = snapshotService.findClosestSnapshot(targetId, diffDates.getStart(), diffDates.getEnd())
                    .map(TrackSnapshot::getTrackData)
                    .map(snap -> TrackData.diff(currentData, snap))
                    .filter(TrackFullData::notEmpty)
                    .orElse(null);

            TrackDiffRecord diffRecord = diffRepository.findByTargetIdAndPeriodStartAndPeriodEnd(targetId,
                    diffDates.getStart(), diffDates.getEnd()).orElseGet(() -> emptyRecord(diffPeriod, diffDates));
            diffRecord.setTrackEnd(now);
            diffRecord.setTrackStart(diffRecord.getTrackStart() != null ? diffRecord.getTrackStart() : now);
            if (diffRecord.getTrackRecord() != null) {
                trackRepository.delete(diffRecord.getTrackRecord());
            }
            diffRecord.setTrackRecord(dataMapper.toTrackRecord(periodDiff));
            diffRepository.save(diffRecord);
        }
    }

    @Override
//    @PostConstruct
    public void updateAll() {
        targetService.getAllTargets().stream()
                .filter(t -> t.getType() == TrackTargetType.ACCOUNT)
                .forEach(t -> updateAccount(t.getId(), t.getName()));
    }

    @PostConstruct
    public void test() {
        var res = snapshotService.findClosestSnapshot(UUID.fromString("93c549cd-f173-41d7-94c5-1aaac895a0b6"),
                LocalDateTime.parse("2022-04-20T14:48:00.000"), LocalDateTime.parse("2022-04-29T14:48:00.000"));
    }

    private TrackDiffRecord emptyRecord(TrackDiffPeriod period, DatePeriod periodDates) {
        var rec = new TrackDiffRecord();
        rec.setPeriodStart(periodDates.getStart());
        rec.setPeriodEnd(periodDates.getEnd());
        rec.setPeriod(period);
        return rec;
    }



}
