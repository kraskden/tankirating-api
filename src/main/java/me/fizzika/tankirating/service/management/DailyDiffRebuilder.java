package me.fizzika.tankirating.service.management;

import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.dto.management.TrackRebuildParams;
import me.fizzika.tankirating.dto.target.TrackTargetDTO;
import me.fizzika.tankirating.mapper.TrackDataMapper;
import me.fizzika.tankirating.mapper.TrackRecordMapper;
import me.fizzika.tankirating.model.TrackData;
import me.fizzika.tankirating.model.track_data.TrackFullData;
import me.fizzika.tankirating.record.tracking.TrackDiffRecord;
import me.fizzika.tankirating.record.tracking.TrackRecord;
import me.fizzika.tankirating.record.tracking.TrackSnapshotRecord;
import me.fizzika.tankirating.repository.tracking.TrackDiffRepository;
import me.fizzika.tankirating.repository.tracking.TrackRepository;
import me.fizzika.tankirating.repository.tracking.TrackSnapshotRepository;
import me.fizzika.tankirating.service.tracking.target.TrackTargetService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toMap;
import static me.fizzika.tankirating.enums.DiffPeriodUnit.DAY;
import static me.fizzika.tankirating.enums.track.TrackFormat.BASE;
import static me.fizzika.tankirating.enums.track.TrackTargetType.ACCOUNT;
import static org.apache.commons.lang3.ObjectUtils.anyNull;

/**
 * Rebuild all day diffs based on snapshots
 */
@Component
@Slf4j
public class DailyDiffRebuilder {

    @Resource
    private TrackSnapshotRepository snapshotRepository;
    @Resource
    private TrackDiffRepository diffRepository;
    @Resource
    private TrackRepository trackRepository;

    @Resource
    private TrackRecordMapper trackRecordMapper;
    @Resource
    private TrackDataMapper dataMapper;

    @Resource
    private TrackTargetService trackTargetService;

    @Resource
    @Lazy
    private DailyDiffRebuilder self;

    public void rebuildDailyDiffs(TrackRebuildParams rebuildParams) {
        List<TrackTargetDTO> accounts = rebuildParams.getAccounts().isEmpty() ?
                trackTargetService.getAll(ACCOUNT) :
                trackTargetService.getAllAccounts(rebuildParams.getAccounts());
        var tasks = accounts.stream()
                .map(acc -> self.rebuildDailyDiffs(acc, rebuildParams.getFrom(), rebuildParams.getTo()))
                .toArray(CompletableFuture[]::new);

        log.info("All tasks are started");
        CompletableFuture.allOf(tasks)
                .join();
    }

    @Async("migrationTaskExecutor")
    @Transactional
    public CompletableFuture<Void> rebuildDailyDiffs(TrackTargetDTO trackTargetDTO, LocalDate from, LocalDate to) {
        Integer targetId = trackTargetDTO.getId();
        Map<LocalDateTime, TrackSnapshotRecord> snapshotMap = snapshotRepository.findAllByDate(targetId, from, to)
                .stream()
                .collect(toMap(TrackSnapshotRecord::getTimestamp, identity()));
        Map<LocalDateTime, TrackDiffRecord> diffMap = diffRepository.findAllDiffsForPeriod(targetId, DAY, from.atStartOfDay(),
                        to.atStartOfDay(), Sort.unsorted(), BASE)
                .stream()
                .collect(toMap(TrackDiffRecord::getPeriodStart, identity()));

        long rebuilded = 0;
        for (var stamp = from; !stamp.isAfter(to); stamp = stamp.plusDays(1)) {
            TrackSnapshotRecord start = snapshotMap.get(stamp.atStartOfDay());
            TrackSnapshotRecord end = snapshotMap.get(stamp.plusDays(1).atStartOfDay());
            TrackDiffRecord diff = diffMap.get(stamp.atStartOfDay());

            if (anyNull(start, end, diff)) {
                continue;
            }

            long timeDiff = end.getTrackRecord().getTime() - start.getTrackRecord().getTime();
            long refTimeDiff = Optional.ofNullable(diff.getTrackRecord())
                    .map(TrackRecord::getTime)
                    .orElse(0L);
            if (timeDiff != refTimeDiff) {
                replaceDiffRecord(diff, start, end);
                rebuilded++;
            } else if (diff.getTrackEnd().isBefore(end.getTimestamp())) {
                diff.setTrackEnd(end.getTimestamp());
                diffRepository.save(diff);
                rebuilded++;
            }
        }
        if (rebuilded > 0) {
            log.warn("Rebuild {} broken diff for user {}", rebuilded,
                    trackTargetDTO.getName());
        } else {
            log.info("Skip {}", trackTargetDTO.getName());
        }
        return completedFuture(null);
    }

    private void replaceDiffRecord(TrackDiffRecord dayDiff, TrackSnapshotRecord start, TrackSnapshotRecord end) {
        TrackFullData startData = trackRecordMapper.toModel(start.getTrackRecord());
        TrackFullData endData = trackRecordMapper.toModel(end.getTrackRecord());

        TrackFullData diffData = TrackData.diff(endData, startData);
        if (dayDiff.getTrackRecord() != null) {
            trackRepository.delete(dayDiff.getTrackRecord());
        }

        dayDiff.setTrackRecord(dataMapper.toTrackRecord(diffData));
        dayDiff.setTrackEnd(end.getTimestamp());
        diffRepository.save(dayDiff);
    }
}