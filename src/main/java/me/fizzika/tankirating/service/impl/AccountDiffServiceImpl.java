package me.fizzika.tankirating.service.impl;

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
import me.fizzika.tankirating.service.AccountDiffService;
import me.fizzika.tankirating.service.tracking.TrackSnapshotService;
import me.fizzika.tankirating.service.tracking.TrackTargetService;
import me.fizzika.tankirating.util.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountDiffServiceImpl implements AccountDiffService {

    private final TrackDiffRepository diffRepository;
    private final TrackDiffMapper diffMapper;

    private final TrackTargetService trackTargetService;
    private final TrackSnapshotService trackSnapshotService;
    private final TrackDataMapper trackDataMapper;

    @Override
    public List<TrackDiffDTO> getAllDiffsForPeriod(String nickname, TrackDiffPeriod period, TrackDatesFilter datesFilter) {
        TrackTargetDTO target = getTrackTarget(nickname);
        return diffRepository.findAllDiffsForPeriod(target.getId(), period, datesFilter.getFrom().atStartOfDay(),
                datesFilter.getTo().atStartOfDay()).stream()
                .map(r -> diffMapper.toDTO(r, target, datesFilter.getFormat()))
                .collect(Collectors.toList());
    }

    @Override
    public TrackDiffDTO getAllTimeDiff(String nickname, TrackFormat format) {
        TrackTargetDTO target = getTrackTarget(nickname);
        return diffRepository.findAllTimeDiff(target.getId())
                .map(r -> diffMapper.toDTO(r, target, format))
                .orElseThrow(() -> accountDiffNotFound(nickname));
    }

    @Override
    public TrackDiffDTO calculateDiffBetweenDates(String nickname, TrackDatesFilter datesFilter) {
        TrackTargetDTO target = getTrackTarget(nickname);
        LocalDateTime from = datesFilter.getFrom().atStartOfDay();
        LocalDateTime to = datesFilter.getTo().atStartOfDay();

        Pair<TrackSnapshot> borderSnapshots = trackSnapshotService.findBorderSnapshots(target.getId(), from, to)
                .orElseThrow(() -> accountDiffNotFound(nickname));
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
        return res;
    }

    @Override
    public TrackDiffDTO getDiffForPeriod(String nickname, TrackDiffPeriod period, Integer offset, TrackFormat format) {
        TrackTargetDTO target = getTrackTarget(nickname);
        LocalDateTime periodStart = period.getDatePeriod(LocalDateTime.now())
                .sub(offset).getStart();
        return diffRepository.findDiffForPeriod(target.getId(), period, periodStart)
                .map(r -> diffMapper.toDTO(r, target, format))
                .orElseThrow(() -> accountDiffNotFound(nickname));
    }

    private TrackTargetDTO getTrackTarget(String nickname) {
        return trackTargetService.getByName(nickname, TrackTargetType.ACCOUNT)
                .orElseThrow(() -> new ExternalException(ExceptionType.ACCOUNT_NOT_FOUND).arg("nickname", nickname));
    }

    private ExternalException accountDiffNotFound(String nickname) {
        return new ExternalException(ExceptionType.ACCOUNT_DIFF_NOT_FOUND)
                .arg("nickname", nickname);
    }

}
