package me.fizzika.tankirating.service.tracking.impl;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.filter.TrackDatesFilter;
import me.fizzika.tankirating.dto.filter.TrackDiffFilter;
import me.fizzika.tankirating.dto.filter.TrackOffsetFilter;
import me.fizzika.tankirating.dto.tracking.TrackActivitiesDTO;
import me.fizzika.tankirating.dto.tracking.TrackDiffDTO;
import me.fizzika.tankirating.dto.tracking.TrackingDTO;
import me.fizzika.tankirating.enums.ExceptionType;
import me.fizzika.tankirating.enums.PeriodUnit;
import me.fizzika.tankirating.enums.track.TrackFormat;
import me.fizzika.tankirating.exceptions.ExternalException;
import me.fizzika.tankirating.mapper.TrackDataMapper;
import me.fizzika.tankirating.mapper.TrackDiffMapper;
import me.fizzika.tankirating.model.TrackSnapshot;
import me.fizzika.tankirating.model.date.DatePeriod;
import me.fizzika.tankirating.model.track_data.TrackFullData;
import me.fizzika.tankirating.repository.tracking.TrackDiffRepository;
import me.fizzika.tankirating.repository.tracking.TrackSnapshotRepository;
import me.fizzika.tankirating.service.tracking.TrackDiffService;
import me.fizzika.tankirating.service.tracking.internal.TrackDataDiffService;
import me.fizzika.tankirating.service.tracking.internal.TrackSnapshotService;
import me.fizzika.tankirating.util.Pair;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static me.fizzika.tankirating.enums.track.TrackFormat.FULL;

@Service
@RequiredArgsConstructor
public class TrackDiffServiceImpl implements TrackDiffService {

    private final TrackDiffRepository diffRepository;
    private final TrackSnapshotRepository snapshotRepository;
    private final TrackDiffMapper diffMapper;

    private final TrackSnapshotService trackSnapshotService;
    private final TrackDataMapper trackDataMapper;
    private final TrackDataDiffService dataDiffService;

    private List<TrackDiffDTO> getAllDiffsForPeriod(Integer targetId, PeriodUnit period, TrackDatesFilter datesFilter) {
        return diffRepository.findAllDiffsForPeriod(targetId, period, datesFilter.getFrom().atStartOfDay(),
                datesFilter.getTo().atStartOfDay(), Sort.by("periodStart"), datesFilter.getFormat()).stream()
                .map(r -> diffMapper.toDTO(r, targetId, datesFilter.getFormat()))
                .collect(Collectors.toList());
    }

    private List<TrackDiffDTO> getAllDiffsForPeriod(Integer targetId, PeriodUnit periodUnit, TrackOffsetFilter offsetFilter) {
        DatePeriod curr = periodUnit.getDatePeriod(LocalDateTime.now());

        TrackDatesFilter datesFilter = new TrackDatesFilter();
        datesFilter.setFormat(offsetFilter.getFormat());
        datesFilter.setFrom(curr.sub(offsetFilter.getOffsetFrom()).getStart().toLocalDate());
        datesFilter.setTo(curr.sub(offsetFilter.getOffsetTo()).getStart().toLocalDate());
        return getAllDiffsForPeriod(targetId, periodUnit, datesFilter);
    }

    @Override
    public List<TrackDiffDTO> getAllDiffsForPeriod(Integer targetId, PeriodUnit period, TrackDiffFilter diffFilter) {
        boolean offsetsPresent = diffFilter.getOffsetTo() != null && diffFilter.getOffsetFrom() != null;
        boolean datesPresent = diffFilter.getFrom() != null && diffFilter.getTo() != null;

        if (offsetsPresent == datesPresent) {
            throw new ExternalException("One filter pair should exists: (from, to) (offsetFrom, offsetTo)", HttpStatus.BAD_REQUEST);
        }

        return datesPresent ? getAllDiffsForPeriod(targetId, period, diffFilter.toDatesFilter()) :
                getAllDiffsForPeriod(targetId, period, diffFilter.toOffsetFilter());
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
                .orElseThrow(() -> trackDiffNotFound(targetId)
                        .arg("periodStart", datesFilter.getFrom())
                        .arg("periodEnd", datesFilter.getTo())
                );

        TrackSnapshot start = borderSnapshots.getFirst();
        TrackSnapshot end = borderSnapshots.getSecond();
        TrackFullData diffData = dataDiffService.diff(end, start);

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
        DatePeriod periodDates = period.getDatePeriod(LocalDateTime.now())
                .sub(offset);

        TrackDiffDTO foundDiff = diffRepository.findDiffForPeriod(targetId, period, periodDates.getStart())
                .map(r -> diffMapper.toDTO(r, targetId, format))
                .orElseThrow(() -> trackDiffNotFound(targetId)
                        .arg("periodStart", periodDates.getStart())
                        .arg("periodEnd", periodDates.getEnd())
                );

        // Full track information may be erased by database sanitizer
        if (format == FULL && missedFullInfo(foundDiff)) {
            TrackDatesFilter datesFilter = new TrackDatesFilter(periodDates.getStart().toLocalDate(),
                    periodDates.getEnd().toLocalDate());
            return calculateDiffBetweenDates(targetId, datesFilter);
        } else {
            return foundDiff;
        }

    }

    private boolean missedFullInfo(TrackDiffDTO foundDiff) {
        return Optional.ofNullable(foundDiff.getTracking())
                .map(TrackingDTO::getActivities)
                .map(TrackActivitiesDTO::getModes)
                .map(List::size)
                .filter(x -> x > 0)
                .isEmpty();
    }

    private ExternalException trackDiffNotFound(Integer targetId) {
        return new ExternalException(ExceptionType.TRACK_DIFF_NOT_FOUND)
                .arg("targetId", targetId);
    }
}
