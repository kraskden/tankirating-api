package me.fizzika.tankirating.service.tracking.impl.impl;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.TrackTargetDTO;
import me.fizzika.tankirating.dto.filter.TrackDatesFilter;
import me.fizzika.tankirating.dto.tracking.TrackDiffDTO;
import me.fizzika.tankirating.enums.ExceptionType;
import me.fizzika.tankirating.enums.TrackFormat;
import me.fizzika.tankirating.enums.track.TrackDiffPeriod;
import me.fizzika.tankirating.enums.track.TrackTargetType;
import me.fizzika.tankirating.exceptions.ExternalException;
import me.fizzika.tankirating.mapper.TrackDataMapper;
import me.fizzika.tankirating.mapper.TrackDiffMapper;
import me.fizzika.tankirating.model.TrackSnapshot;
import me.fizzika.tankirating.model.track_data.TrackFullData;
import me.fizzika.tankirating.repository.TrackDiffRepository;
import me.fizzika.tankirating.repository.TrackSnapshotRepository;
import me.fizzika.tankirating.service.tracking.TrackDiffService;
import me.fizzika.tankirating.service.tracking.internal.TrackSnapshotService;
import me.fizzika.tankirating.service.tracking.TrackTargetService;
import me.fizzika.tankirating.util.Pair;
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
    public List<TrackDiffDTO> getAllDiffsForPeriod(TrackTargetDTO target, TrackDiffPeriod period, TrackDatesFilter datesFilter) {
        return diffRepository.findAllDiffsForPeriod(target.getId(), period, datesFilter.getFrom().atStartOfDay(),
                datesFilter.getTo().atStartOfDay()).stream()
                .map(r -> diffMapper.toDTO(r, target, datesFilter.getFormat()))
                .collect(Collectors.toList());
    }

    @Override
    public TrackDiffDTO getAllTimeDiff(TrackTargetDTO target, TrackFormat format) {
        return diffRepository.findAllTimeDiff(target.getId())
                .map(r -> diffMapper.toDTO(r, target, format))
                .orElseThrow(() -> accountDiffNotFound(target.getName()));
    }

    @Override
    public TrackDiffDTO calculateDiffBetweenDates(TrackTargetDTO target, TrackDatesFilter datesFilter) {
        LocalDateTime from = datesFilter.getFrom().atStartOfDay();
        LocalDateTime to = datesFilter.getTo().atStartOfDay();

        Pair<TrackSnapshot> borderSnapshots = trackSnapshotService.findBorderSnapshots(target.getId(), from, to)
                .orElseThrow(() -> accountDiffNotFound(target.getName()));
        TrackSnapshot start = borderSnapshots.getFirst();
        TrackSnapshot end = borderSnapshots.getSecond();

        TrackFullData diffData = end.getTrackData();
        diffData.sub(start.getTrackData());

        TrackDiffDTO res = new TrackDiffDTO();
        res.setPeriodStart(from);
        res.setPeriodEnd(to);
        res.setTrackStart(start.getTimestamp());
        res.setTrackEnd(end.getTimestamp());
        res.setTracking(trackDataMapper.toFullDTO(diffData, target));
        res.setPremiumDays(snapshotRepository.getPremiumDays(target.getId(), from, to));
        return res;
    }

    @Override
    public TrackDiffDTO getDiffForPeriod(TrackTargetDTO target, TrackDiffPeriod period, Integer offset, TrackFormat format) {
        LocalDateTime periodStart = period.getDatePeriod(LocalDateTime.now())
                .sub(offset).getStart();
        return diffRepository.findDiffForPeriod(target.getId(), period, periodStart)
                .map(r -> diffMapper.toDTO(r, target, format))
                .orElseThrow(() -> accountDiffNotFound(target.getName()));
    }

    private ExternalException accountDiffNotFound(String name) {
        return new ExternalException(ExceptionType.TRACK_DIFF_NOT_FOUND)
                .arg("name", name);
    }

}
