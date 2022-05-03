package me.fizzika.tankirating.service.impl;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.TrackTargetDTO;
import me.fizzika.tankirating.dto.filter.TrackDatesFilter;
import me.fizzika.tankirating.dto.tracking.TrackDiffDTO;
import me.fizzika.tankirating.enums.ExceptionType;
import me.fizzika.tankirating.enums.TrackFormat;
import me.fizzika.tankirating.enums.track.TrackDiffPeriod;
import me.fizzika.tankirating.exceptions.ExternalException;
import me.fizzika.tankirating.mapper.TrackDiffMapper;
import me.fizzika.tankirating.repository.TrackDiffRepository;
import me.fizzika.tankirating.service.AccountDiffService;
import me.fizzika.tankirating.service.tracking.TrackTargetService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountDiffServiceImpl implements AccountDiffService {

    private final TrackDiffRepository diffRepository;
    private final TrackDiffMapper diffMapper;
    private final TrackTargetService trackTargetService;

    @Override
    public List<TrackDiffDTO> findDiffsForPeriod(String nickname, TrackDiffPeriod period, TrackDatesFilter datesFilter) {
        TrackTargetDTO target = getTrackTarget(nickname);
        return diffRepository.findDiffs(target.getId(), period, datesFilter.getFrom().atStartOfDay(),
                datesFilter.getTo().atStartOfDay()).stream()
                .map(r -> diffMapper.toDTO(r, target, datesFilter.getFormat()))
                .collect(Collectors.toList());
    }

    @Override
    public TrackDiffDTO getAllTimeDiff(String nickname, TrackFormat format) {
        TrackTargetDTO target = getTrackTarget(nickname);
        return diffRepository.findAllTimeDiff(target.getId())
                .map(r -> diffMapper.toDTO(r, target, format))
                .orElseThrow(() -> new ExternalException(ExceptionType.ACCOUNT_DIFF_NOT_FOUND)
                        .arg("nickname", nickname));
    }

    @Override
    public TrackDiffDTO calculateDiffBetweenDates(String nickname, TrackDatesFilter datesFilter) {
        return null;
    }

    private TrackTargetDTO getTrackTarget(String nickname) {
        return trackTargetService.getByName(nickname)
                .orElseThrow(() -> new ExternalException(ExceptionType.ACCOUNT_NOT_FOUND).arg("nickname", nickname));
    }

}
