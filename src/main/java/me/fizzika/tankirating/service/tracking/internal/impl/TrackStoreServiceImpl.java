package me.fizzika.tankirating.service.tracking.internal.impl;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.tracking.TrackEntityDTO;
import me.fizzika.tankirating.enums.PeriodUnit;
import me.fizzika.tankirating.enums.track.GroupMeta;
import me.fizzika.tankirating.enums.track.TankiEntityType;
import me.fizzika.tankirating.mapper.TrackDataMapper;
import me.fizzika.tankirating.model.EntityActivityTrack;
import me.fizzika.tankirating.model.TrackData;
import me.fizzika.tankirating.model.TrackGroup;
import me.fizzika.tankirating.model.TrackSnapshot;
import me.fizzika.tankirating.model.date.DatePeriod;
import me.fizzika.tankirating.model.date.DateRange;
import me.fizzika.tankirating.model.date.PeriodDiffDates;
import me.fizzika.tankirating.model.track_data.TrackActivityData;
import me.fizzika.tankirating.model.track_data.TrackFullData;
import me.fizzika.tankirating.model.track_data.TrackPlayData;
import me.fizzika.tankirating.record.tracking.TrackDiffRecord;
import me.fizzika.tankirating.record.tracking.TrackSnapshotRecord;
import me.fizzika.tankirating.record.tracking.TrackTargetRecord;
import me.fizzika.tankirating.repository.tracking.TrackDiffRepository;
import me.fizzika.tankirating.repository.tracking.TrackRepository;
import me.fizzika.tankirating.repository.tracking.TrackSnapshotRepository;
import me.fizzika.tankirating.service.tracking.internal.TrackEntityService;
import me.fizzika.tankirating.service.tracking.internal.TrackSnapshotService;
import me.fizzika.tankirating.service.tracking.internal.TrackStoreService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrackStoreServiceImpl implements TrackStoreService {

    private final TrackSnapshotRepository snapshotRepository;
    private final TrackDiffRepository diffRepository;
    private final TrackRepository trackRepository;

    private final TrackSnapshotService snapshotService;
    private final TrackEntityService entityService;

    private final TrackDataMapper dataMapper;

    @Override
    @Transactional
    public void updateTargetData(Integer targetId, TrackFullData currentData, boolean hasPremium) {
        LocalDateTime now = LocalDateTime.now();

        // Save current snapshot
        updateSnapshots(targetId, now, currentData, hasPremium);

        // Update diff for each period
        for (PeriodUnit diffPeriod : PeriodUnit.values()) {
            updateDiff(targetId, now, currentData, diffPeriod, hasPremium);
        }
    }

    @Override
    public void updateCurrentGroupData(TrackGroup group) {
        LocalDateTime now = LocalDateTime.now();
        for (PeriodUnit diffPeriod : PeriodUnit.GROUP_PERIODS) {
            updateGroupDiff(group, now, diffPeriod);
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
    private void updateSnapshots(Integer targetId, LocalDateTime now, TrackFullData data, boolean hasPremium) {
        LocalDateTime dayStart = now.truncatedTo(ChronoUnit.DAYS);

        if (snapshotService.exists(targetId, dayStart)) {
            Optional<TrackSnapshotRecord> optHeadSnapshot = snapshotRepository.findLastSnapshot(targetId,
                    dayStart.plusSeconds(1), now);
            if (optHeadSnapshot.isPresent()) {
                updateHeadSnapshot(optHeadSnapshot.get(), targetId, now, data, hasPremium);
            } else {
                snapshotService.save(new TrackSnapshot(targetId, now, data, hasPremium));
            }
        } else {
            createDaySnapshot(targetId, dayStart, data, hasPremium);
        }
    }

    private void updateDiff(Integer targetId, LocalDateTime now, TrackFullData currentData, PeriodUnit diffPeriod,
                            boolean hasPremium) {

        Integer maxScore = currentData.getBase().getScore();

        DatePeriod diffDates = diffPeriod.getDatePeriod(now);

        int premiumDays = diffPeriod == PeriodUnit.DAY ?
                hasPremium ? 1 : 0
                : snapshotRepository.getPremiumDays(targetId, diffDates.getStart(), diffDates.getEnd());

        Optional<TrackFullData> periodDiff = snapshotService.findFirstInRange(targetId, diffDates.getStart(), diffDates.getEnd())
                .map(TrackSnapshot::getTrackData)
                .map(snap -> TrackData.diff(currentData, snap))
                .filter(TrackFullData::notEmpty);

        TrackDiffRecord diffRecord = getOrCreateDiffRecord(targetId, diffPeriod, diffDates);
        diffRecord.setTrackEnd(now);
        diffRecord.setTrackStart(diffRecord.getTrackStart() != null ? diffRecord.getTrackStart() : now);
        diffRecord.setPremiumDays(premiumDays);
        diffRecord.setMaxScore(maxScore);

        if (diffRecord.getTrackRecord() != null) {
            if (periodDiff.isPresent() && periodDiff.get().getBase().getTime() == diffRecord.getTrackRecord().getTime()) {
                // Don't rewrite track data if it's not changed, just update trackStart, trackEnd && premium
                diffRepository.save(diffRecord);
                return;
            }
            // Delete old track data
            trackRepository.delete(diffRecord.getTrackRecord());
        }
        diffRecord.setTrackRecord(periodDiff.map(dataMapper::toTrackRecord).orElse(null));
        diffRepository.save(diffRecord);
    }

    private void updateGroupDiff(TrackGroup group, LocalDateTime now, PeriodUnit diffPeriod) {
        PeriodDiffDates diffDates = new PeriodDiffDates(diffPeriod.getDatePeriod(now), new DateRange(now, now));
        updateGroupDiff(group, diffPeriod, diffDates);
    }

    @Override
    public void updateGroupDiff(TrackGroup group, PeriodUnit diffPeriod, PeriodDiffDates diffDates) {
        TrackDiffRecord diffRecord = getOrCreateDiffRecord(group.getId(), diffPeriod, diffDates.toPeriodRange());

        diffRecord.setTrackEnd(diffDates.getTrackEnd());
        diffRecord.setTrackStart(diffRecord.getTrackStart() != null ? diffRecord.getTrackStart() :
                diffDates.getTrackStart());
        if (diffRecord.getTrackRecord() != null) {
            trackRepository.delete(diffRecord.getTrackRecord());
        }
        TrackFullData diffData = getActivityGroupData(diffDates.toPeriodRange(), diffPeriod, group.getMeta());
        diffRecord.setTrackRecord(dataMapper.toTrackRecord(diffData));
        diffRepository.save(diffRecord);
    }

    private TrackDiffRecord getOrCreateDiffRecord(Integer targetId, PeriodUnit diffPeriod, DateRange diffDates) {
        return diffRepository.findByTargetIdAndPeriodStartAndPeriodEnd(targetId,
                diffDates.getStart(), diffDates.getEnd()).orElseGet(() -> emptyRecord(targetId, diffPeriod, diffDates));
    }

    private void createDaySnapshot(Integer targetId, LocalDateTime dayStart, TrackFullData data, boolean hasPremium) {
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
                dayRec.setHasPremium(hasPremium);
                snapshotRepository.save(dayRec);
                return;
            }
        }
        snapshotService.save(new TrackSnapshot(targetId, dayStart, data, hasPremium));
    }

    private void updateHeadSnapshot(TrackSnapshotRecord headRecord, Integer targetId, LocalDateTime now,
                                    TrackFullData data, boolean hasPremium) {
        if (data.getBase().getTime() == getTime(headRecord))  {
            // User is not played, just update timestamp and premium for HEAD snapshot, don't change data
            headRecord.setTimestamp(now);
            headRecord.setHasPremium(hasPremium);
            snapshotRepository.save(headRecord);
        } else {
            snapshotService.save(new TrackSnapshot(targetId, now, data, hasPremium));

            // Old HEAD snapshot is not needed anymore, delete it
            if (headRecord.getTrackRecord() != null) {
                trackRepository.delete(headRecord.getTrackRecord());
            }
            snapshotRepository.delete(headRecord);
        }
    }

    private TrackFullData getActivityGroupData(DateRange diffDates, PeriodUnit diffPeriod, GroupMeta group) {
        Map<TankiEntityType, TrackActivityData> activityMap = new EnumMap<>(TankiEntityType.class);
        for (TankiEntityType e : TankiEntityType.values()) {
            activityMap.put(e, new TrackActivityData());
        }

        List<EntityActivityTrack> activityStat = diffRepository.getActivityStat(diffPeriod, diffDates.getStart(),
                        group.getMinScore(), group.getMaxScore());

        activityStat
                .forEach(tr -> {
                    TrackEntityDTO trackEntity = entityService.get(tr.getEntityId());
                    activityMap.get(trackEntity.getType()).set(trackEntity.getName(),
                            new TrackPlayData(tr.getScore(), tr.getTime()));
                });

        var res = new TrackFullData();
        res.setActivities(activityMap);
        return res;
    }

    private TrackDiffRecord emptyRecord(Integer targetId, PeriodUnit period, DateRange periodDates) {
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
