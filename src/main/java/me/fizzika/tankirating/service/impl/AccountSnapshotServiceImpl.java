package me.fizzika.tankirating.service.impl;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.TrackTargetDTO;
import me.fizzika.tankirating.dto.tracking.TrackSnapshotDTO;
import me.fizzika.tankirating.enums.ExceptionType;
import me.fizzika.tankirating.enums.TrackFormat;
import me.fizzika.tankirating.exceptions.ExternalException;
import me.fizzika.tankirating.mapper.TrackSnapshotMapper;
import me.fizzika.tankirating.repository.TrackSnapshotRepository;
import me.fizzika.tankirating.service.AccountSnapshotService;
import me.fizzika.tankirating.service.tracking.TrackTargetService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountSnapshotServiceImpl implements AccountSnapshotService {

    private final TrackTargetService trackTargetService;
    private final TrackSnapshotRepository snapshotRepository;
    private final TrackSnapshotMapper snapshotMapper;

    @Override
    public TrackSnapshotDTO getLatestSnapshot(String nickname, TrackFormat format) {
        TrackTargetDTO target = getTrackTarget(nickname);
        return snapshotRepository.getLatest(target.getId())
                .map(r -> snapshotMapper.toDTO(r, target, format))
                .orElseThrow(() -> snapshotNotFound(nickname));
    }

    @Override
    public TrackSnapshotDTO getInitSnapshot(String nickname, TrackFormat format) {
        TrackTargetDTO target = getTrackTarget(nickname);
        return snapshotRepository.getInit(target.getId())
                .map(r -> snapshotMapper.toDTO(r, target, format))
                .orElseThrow(() -> snapshotNotFound(nickname));
    }

    private TrackTargetDTO getTrackTarget(String nickname) {
        return trackTargetService.getByName(nickname)
                .orElseThrow(() -> new ExternalException(ExceptionType.ACCOUNT_NOT_FOUND).arg("nickname", nickname));
    }

    private ExternalException snapshotNotFound(String nickname) {
        return new ExternalException(ExceptionType.ACCOUNT_SNAPSHOT_NOT_FOUND)
                .arg("nickname", nickname);
    }

}
