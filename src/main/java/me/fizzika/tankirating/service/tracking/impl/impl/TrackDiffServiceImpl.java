package me.fizzika.tankirating.service.tracking.impl.impl;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.filter.TrackDatesFilter;
import me.fizzika.tankirating.dto.tracking.TrackDiffDTO;
import me.fizzika.tankirating.enums.ExceptionType;
import me.fizzika.tankirating.enums.TrackFormat;
import me.fizzika.tankirating.enums.PeriodUnit;
import me.fizzika.tankirating.exceptions.ExternalException;
import me.fizzika.tankirating.mapper.TrackDataMapper;
import me.fizzika.tankirating.mapper.TrackDiffMapper;
import me.fizzika.tankirating.model.TrackSnapshot;
import me.fizzika.tankirating.model.track_data.TrackFullData;
import me.fizzika.tankirating.repository.tracking.TrackDiffRepository;
import me.fizzika.tankirating.repository.tracking.TrackSnapshotRepository;
import me.fizzika.tankirating.service.tracking.TrackDiffService;
import me.fizzika.tankirating.service.tracking.internal.TrackSnapshotService;
import me.fizzika.tankirating.util.Pair;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrackDiffServiceImpl implements TrackDiffService {

    private final TrackDiffRepository diffRepository;
    private final TrackSnapshotRepository snapshotRepository;
    private final TrackDiffMapper diffMapper;

    private final TrackSnapshotService trackSnapshotService;
    private final TrackDataMapper trackDataMapper;

    @Override
    public List<TrackDiffDTO> getAllDiffsForPeriod(Integer targetId, PeriodUnit period, TrackDatesFilter datesFilter) {
        return diffRepository.findAllDiffsForPeriod(targetId, period, datesFilter.getFrom().atStartOfDay(),
                datesFilter.getTo().atStartOfDay(), Sort.by("periodStart")).stream()
                .map(r -> diffMapper.toDTO(r, targetId, datesFilter.getFormat()))
                .collect(Collectors.toList());
    }

    @Override
    public TrackDiffDTO getAllTimeDiff(Integer targetId, TrackFormat format) {
        return diffRepository.findAllTimeDiff(targetId)
                .map(r -> diffMapper.toDTO(r, targetId, format))
                .orElseThrow(() -> trackDiffNotFound(targetId));
    }

    @Override
    public TrackDiffDTO calculateDiffBetweenDates(Integer targetId, TrackDatesFilter datesFilter) {
        LocalDateTime from = datesFilter.getFrom().atStartOfDay();
        LocalDateTime to = datesFilter.getTo().atStartOfDay();

        Pair<TrackSnapshot> borderSnapshots = trackSnapshotService.findBorderSnapshots(targetId, from, to)
                .orElseThrow(() -> trackDiffNotFound(targetId));
        TrackSnapshot start = borderSnapshots.getFirst();
        TrackSnapshot end = borderSnapshots.getSecond();

        TrackFullData diffData = end.getTrackData();
        diffData.sub(start.getTrackData());

        TrackDiffDTO res = new TrackDiffDTO();
        res.setPeriodStart(from);
        res.setPeriodEnd(to);
        res.setTrackStart(start.getTimestamp());
        res.setTrackEnd(end.getTimestamp());
        res.setTracking(trackDataMapper.toFullDTO(diffData, targetId));
        res.setPremiumDays(snapshotRepository.getPremiumDays(targetId, from, to));
        return res;
    }

    @Override
    public TrackDiffDTO getDiffForPeriod(Integer targetId, PeriodUnit period, Integer offset, TrackFormat format) {
        LocalDateTime periodStart = period.getDatePeriod(LocalDateTime.now())
                .sub(offset).getStart();
        return diffRepository.findDiffForPeriod(targetId, period, periodStart)
                .map(r -> diffMapper.toDTO(r, targetId, format))
                .orElseThrow(() -> trackDiffNotFound(targetId));
    }

    private ExternalException trackDiffNotFound(Integer targetId) {
        return new ExternalException(ExceptionType.TRACK_DIFF_NOT_FOUND)
                .arg("targetId", targetId);
    }

}
