package me.fizzika.tankirating.service.tracking.internal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.dto.tracking.TrackEntityDTO;
import me.fizzika.tankirating.enums.PeriodUnit;
import me.fizzika.tankirating.enums.SnapshotState;
import me.fizzika.tankirating.enums.track.GroupMeta;
import me.fizzika.tankirating.enums.track.TankiEntityType;
import me.fizzika.tankirating.exceptions.tracking.InvalidDiffException;
import me.fizzika.tankirating.exceptions.tracking.InvalidTrackDataException;
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
import me.fizzika.tankirating.repository.tracking.TrackDiffRepository;
import me.fizzika.tankirating.repository.tracking.TrackRepository;
import me.fizzika.tankirating.repository.tracking.TrackSnapshotRepository;
import me.fizzika.tankirating.repository.tracking.TrackTargetRepository;
import me.fizzika.tankirating.service.tracking.internal.TrackEntityService;
import me.fizzika.tankirating.service.tracking.internal.TrackSnapshotService;
import me.fizzika.tankirating.service.tracking.internal.TrackStoreService;
import me.fizzika.tankirating.util.TrackUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackStoreServiceImpl implements TrackStoreService {

    private final TrackSnapshotRepository snapshotRepository;
    private final TrackDiffRepository diffRepository;
    private final TrackRepository trackRepository;
    private final TrackTargetRepository targetRepository;

    private final TrackSnapshotService snapshotService;
    private final TrackEntityService entityService;

    private final TrackDataMapper dataMapper;

    @Value("${app.tracking.time-inaccuracy-interval}")
    private Duration timeInaccuracyInterval;

    @Override
    @Transactional
    public void updateTargetData(Integer targetId, TrackFullData currentData, boolean hasPremium)
            throws InvalidTrackDataException {
        log.debug("[{}] Updating target", targetId);
        log.trace("Target: {}, Prem: {}, Data: {}", targetId, hasPremium, currentData);

        LocalDateTime now = LocalDateTime.now();

        // Save current snapshot
        SnapshotState snapshotState = createOrUpdateSnapshot(targetId, now, currentData, hasPremium);

        updateDiffs(targetId, now, currentData, hasPremium);
        if (snapshotState == SnapshotState.CREATED) {
            LocalDateTime prevDayTime = now.truncatedTo(ChronoUnit.DAYS).minusSeconds(1);
            updateDiffs(targetId, prevDayTime, currentData, hasPremium);
        }
    }

    @Override
    @Transactional
    public void updateCurrentGroupData(TrackGroup group) {
        LocalDateTime now = LocalDateTime.now();
        for (PeriodUnit diffPeriod : PeriodUnit.GROUP_PERIODS) {
            updateGroupDiff(group, now, diffPeriod);
        }
    }

    private void updateDiffs(Integer targetId, LocalDateTime now, TrackFullData currentData, boolean hasPremium) {
        for (PeriodUnit diffPeriod : PeriodUnit.values()) {
            updateDiff(targetId, now, currentData, diffPeriod, hasPremium);
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
    private SnapshotState createOrUpdateSnapshot(Integer targetId, LocalDateTime now, TrackFullData data, boolean hasPremium) {
        LocalDateTime dayStart = now.truncatedTo(ChronoUnit.DAYS);

        if (snapshotService.exists(targetId, dayStart)) {
            Optional<TrackSnapshotRecord> optHeadSnapshot = snapshotRepository.findLastSnapshot(targetId,
                    dayStart.plusSeconds(1), now);
            if (optHeadSnapshot.isPresent()) {
                log.debug("[{}] Head snapshot is exists, updating head snapshot (id={})", targetId, optHeadSnapshot.get().getId());
                updateHeadSnapshot(optHeadSnapshot.get(), targetId, now, data, hasPremium);
            } else {
                long id = snapshotService.save(new TrackSnapshot(targetId, now, data, hasPremium));
                log.debug("[{}] Head snapshot isn't exists, create it (id={})", targetId, id);
            }
            return SnapshotState.UPDATED;
        } else {
            createDaySnapshot(targetId, dayStart, data, hasPremium);
            return SnapshotState.CREATED;
        }
    }

    private void updateDiff(Integer targetId, LocalDateTime now, TrackFullData currentData, PeriodUnit diffPeriod,
                            boolean hasPremium) throws InvalidDiffException {

        log.debug("[{}] Updating diff {}", targetId, diffPeriod);
        Integer maxScore = currentData.getBase().getScore();

        DatePeriod diffDates = diffPeriod.getDatePeriod(now);
        log.debug("[{}] Dates: {} - {}", targetId, diffDates.getStart(), diffDates.getEnd());

        int premiumDays = diffPeriod == PeriodUnit.DAY ?
                hasPremium ? 1 : 0
                : snapshotRepository.getPremiumDays(targetId, diffDates.getStart(), diffDates.getEnd());
        log.debug("[{}] Premium days: {}", targetId, premiumDays);

        Optional<TrackFullData> baseSnapshot = snapshotService.findFirstInRange(targetId, diffDates.getStart(), diffDates.getEnd())
                .map(TrackSnapshot::getTrackData);
        if (baseSnapshot.isPresent()) {
            log.debug("[{}] Base snapshot: {}", targetId, baseSnapshot);
        }

        Optional<TrackFullData> periodDiff = baseSnapshot
                .map(snap -> TrackData.diff(currentData, snap))
                .filter(TrackFullData::notEmpty);

        periodDiff.ifPresent(d -> TrackUtils.validateDiffData(d, diffPeriod.getChronoUnit().getDuration().toSeconds(),
                        timeInaccuracyInterval));

        log.debug("[{}] DiffTime: {}", targetId, periodDiff.map(d -> d.getBase().getTime()).orElse(0L));

        TrackDiffRecord diffRecord = getOrCreateDiffRecord(targetId, diffPeriod, diffDates);
        diffRecord.setTrackEnd(now);
        diffRecord.setTrackStart(diffRecord.getTrackStart() != null ? diffRecord.getTrackStart() : now);
        diffRecord.setPremiumDays(premiumDays);
        diffRecord.setMaxScore(maxScore);

        if (diffRecord.getTrackRecord() != null) {
            if (periodDiff.isPresent() && periodDiff.get().getBase().getTime() == diffRecord.getTrackRecord().getTime()) {
                log.debug("[{}] Diff isn't changed, update track timestamps in existing (id={})", targetId, diffRecord.getId());
                // Don't rewrite track data if it's not changed, just update trackStart, trackEnd && premium
                diffRepository.save(diffRecord);
                return;
            }
            log.debug("[{}] Deleting old diff data (id={})", targetId, diffRecord.getTrackRecord().getId());
            // Delete old track data
            trackRepository.delete(diffRecord.getTrackRecord());
        }
        diffRecord.setTrackRecord(periodDiff.map(dataMapper::toTrackRecord).orElse(null));
        diffRepository.save(diffRecord);
        log.debug("[{}] Diff has been saved (id={})", targetId, diffRecord.getId());
    }

    private void updateGroupDiff(TrackGroup group, LocalDateTime now, PeriodUnit diffPeriod) {
        log.debug("[{}] Updating diff {}", group, diffPeriod);
        PeriodDiffDates diffDates = new PeriodDiffDates(diffPeriod.getDatePeriod(now), new DateRange(now, now));
        log.debug("[{}] Diff dates: \nPeriod: {} - {}\nTrack: {} - {}", group,
                diffDates.getPeriodStart(), diffDates.getPeriodEnd(),
                diffDates.getTrackStart(), diffDates.getTrackEnd());
        updateGroupDiff(group, diffPeriod, diffDates);
    }

    @Override
    public void updateGroupDiff(TrackGroup group, PeriodUnit diffPeriod, PeriodDiffDates diffDates) {
        TrackDiffRecord diffRecord = getOrCreateDiffRecord(group.getId(), diffPeriod, diffDates.toPeriodRange());

        diffRecord.setTrackEnd(diffDates.getTrackEnd());
        diffRecord.setTrackStart(diffRecord.getTrackStart() != null ? diffRecord.getTrackStart() :
                diffDates.getTrackStart());
        if (diffRecord.getTrackRecord() != null) {
            log.debug("[{}] Delete old diff record (id={})", group, diffRecord.getTrackRecord().getId());
            trackRepository.delete(diffRecord.getTrackRecord());
        }

        TrackFullData diffData = getActivityGroupData(diffDates.toPeriodRange(), diffPeriod, group.getMeta());
        log.debug("[{}] Track time: {}", group, diffData.getBase().getTime());
        diffRecord.setTrackRecord(dataMapper.toTrackRecord(diffData));
        var saved = diffRepository.save(diffRecord);
        log.debug("[{}] Diff has been saved (id={})", group, saved.getId());
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
                log.debug("[{}] Reuse snapshot data from previous day snapshot (id={})",
                        targetId, prevSnapshot.getId());
                // User is not played in this day, reuse previous snapshot data
                TrackSnapshotRecord dayRec = new TrackSnapshotRecord();
                dayRec.setTrackRecord(prevSnapshot.getTrackRecord());
                dayRec.setTarget(prevSnapshot.getTarget());
                dayRec.setTimestamp(dayStart);
                dayRec.setHasPremium(hasPremium);
                var created = snapshotRepository.save(dayRec);
                log.debug("[{}] Created snapshot (id={})", targetId, created.getId());
                return;
            }
        }
        long id = snapshotService.save(new TrackSnapshot(targetId, dayStart, data, hasPremium));
        log.debug("[{}] Created snapshot (id={})", targetId, id);
    }

    private void updateHeadSnapshot(TrackSnapshotRecord headRecord, Integer targetId, LocalDateTime now,
                                    TrackFullData data, boolean hasPremium) {
        if (data.getBase().getTime() == getTime(headRecord))  {
            log.debug("[{}] Target is not played, just update timestamp for HEAD snapshot (id={})", targetId,
                    headRecord.getId());
            // User is not played, just update timestamp and premium for HEAD snapshot, don't change data
            headRecord.setTimestamp(now);
            headRecord.setHasPremium(hasPremium);
            snapshotRepository.save(headRecord);
        } else {
            long id = snapshotService.save(new TrackSnapshot(targetId, now, data, hasPremium));

            // Old HEAD snapshot is not needed anymore, delete it
            if (headRecord.getTrackRecord() != null) {
                trackRepository.delete(headRecord.getTrackRecord());
            }
            snapshotRepository.delete(headRecord);
            log.debug("[{}] Replace old HEAD snapshot (id={}) with new (id={})", targetId, headRecord.getId(),
                    id);
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
        rec.setTarget(targetRepository.getReferenceById(targetId));
        rec.setPeriodStart(periodDates.getStart());
        rec.setPeriodEnd(periodDates.getEnd());
        rec.setPeriod(period);
        return rec;
    }

    private long getTime(TrackSnapshotRecord rec) {
        return rec.getTrackRecord() == null ? 0 : rec.getTrackRecord().getTime();
    }

}
