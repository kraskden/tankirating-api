package me.fizzika.tankirating.service.tracking.impl;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.enums.track.TrackDiffPeriod;
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
import me.fizzika.tankirating.service.tracking.TargetTrackingService;
import me.fizzika.tankirating.service.tracking.TrackSnapshotService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TargetTrackingServiceImpl implements TargetTrackingService {

    private final TrackSnapshotRepository snapshotRepository;
    private final TrackDiffRepository diffRepository;
    private final TrackRepository trackRepository;

    private final TrackSnapshotService snapshotService;
    private final TrackDataMapper dataMapper;

    @Override
    @Transactional
    public void updateTargetData(Integer targetId, TrackFullData currentData) {
        LocalDateTime now = LocalDateTime.now();

        // Save current snapshot
        updateSnapshots(targetId, now, currentData);

        // Update diff for each period
        for (TrackDiffPeriod diffPeriod : TrackDiffPeriod.values()) {
            updateDiff(targetId, now, currentData, diffPeriod);
        }
    }

    /**
     * Create or Update *HEAD* and *DAY_START* snapshots.
     * <br/>
     * <br/>
     * HEAD snapshot it is the most recent snapshot in the day
     * <br/>
     * DAY_START snapshot it is the first snapshot in the day
     */
    private void updateSnapshots(Integer targetId, LocalDateTime now, TrackFullData data) {
        LocalDateTime dayStart = now.truncatedTo(ChronoUnit.DAYS);

        if (snapshotService.exists(targetId, dayStart)) {
            Optional<TrackSnapshotRecord> optHeadSnapshot = snapshotRepository.findLastSnapshot(targetId,
                    dayStart.plusSeconds(1), now);
            if (optHeadSnapshot.isPresent()) {
                updateHeadSnapshot(optHeadSnapshot.get(), targetId, now, data);
            } else {
                snapshotService.save(new TrackSnapshot(targetId, now, data));
            }
        } else {
            createDaySnapshot(targetId, dayStart, data);
        }
    }

    private void updateDiff(Integer targetId, LocalDateTime now, TrackFullData currentData, TrackDiffPeriod diffPeriod) {
        DatePeriod diffDates = diffPeriod.getDatePeriod(now);

        Optional<TrackFullData> periodDiff = snapshotService.findFirstInRange(targetId, diffDates.getStart(), diffDates.getEnd())
                .map(TrackSnapshot::getTrackData)
                .map(snap -> TrackData.diff(currentData, snap))
                .filter(TrackFullData::notEmpty);

        TrackDiffRecord diffRecord = diffRepository.findByTargetIdAndPeriodStartAndPeriodEnd(targetId,
                diffDates.getStart(), diffDates.getEnd()).orElseGet(() -> emptyRecord(targetId, diffPeriod, diffDates));
        diffRecord.setTrackEnd(now);
        diffRecord.setTrackStart(diffRecord.getTrackStart() != null ? diffRecord.getTrackStart() : now);
        if (diffRecord.getTrackRecord() != null) {
            if (periodDiff.isPresent() && periodDiff.get().getBase().getTime() == diffRecord.getTrackRecord().getTime()) {
                // Don't rewrite track data if it's not changed, just update trackStart and trackEnd
                diffRepository.save(diffRecord);
                return;
            }
            // Delete old track data
            trackRepository.delete(diffRecord.getTrackRecord());
        }
        diffRecord.setTrackRecord(periodDiff.map(dataMapper::toTrackRecord).orElse(null));
        diffRepository.save(diffRecord);
    }

    private void createDaySnapshot(Integer targetId, LocalDateTime dayStart, TrackFullData data) {
        LocalDateTime prevDay = dayStart.minusDays(1);
        Optional<TrackSnapshotRecord> optPrevDaySnapshot = snapshotRepository.findOneByTargetIdAndTimestamp(targetId, prevDay);
        if (optPrevDaySnapshot.isPresent()) {
            TrackSnapshotRecord prevSnapshot = optPrevDaySnapshot.get();
            if (getTime(prevSnapshot) == data.getBase().getTime()) {
                // User is not played in this day, reuse previous snapshot data
                TrackSnapshotRecord dayRec = new TrackSnapshotRecord();
                dayRec.setTrackRecord(prevSnapshot.getTrackRecord());
                dayRec.setTarget(prevSnapshot.getTarget());
                dayRec.setTimestamp(dayStart);
                snapshotRepository.save(dayRec);
                return;
            }
        }
        snapshotService.save(new TrackSnapshot(targetId, dayStart, data));
    }

    private void updateHeadSnapshot(TrackSnapshotRecord headRecord, Integer targetId, LocalDateTime now, TrackFullData data) {
        if (data.getBase().getTime() == getTime(headRecord))  {
            // User is not played, just update timestamp for HEAD snapshot, don't change data
            headRecord.setTimestamp(now);
            snapshotRepository.save(headRecord);
        } else {
            snapshotService.save(new TrackSnapshot(targetId, now, data));

            // Old HEAD snapshot is not needed anymore, delete it
            if (headRecord.getTrackRecord() != null) {
                trackRepository.delete(headRecord.getTrackRecord());
            }
            snapshotRepository.delete(headRecord);
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

    private long getTime(TrackSnapshotRecord rec) {
        return rec.getTrackRecord() == null ? 0 : rec.getTrackRecord().getTime();
    }

}
