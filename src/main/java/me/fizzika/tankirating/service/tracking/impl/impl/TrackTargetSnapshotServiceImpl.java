package me.fizzika.tankirating.service.tracking.impl.impl;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.TrackTargetDTO;
import me.fizzika.tankirating.dto.tracking.TrackSnapshotDTO;
import me.fizzika.tankirating.enums.ExceptionType;
import me.fizzika.tankirating.enums.TrackFormat;
import me.fizzika.tankirating.exceptions.ExternalException;
import me.fizzika.tankirating.mapper.TrackSnapshotMapper;
import me.fizzika.tankirating.repository.tracking.TrackSnapshotRepository;
import me.fizzika.tankirating.service.tracking.TrackTargetSnapshotService;
import me.fizzika.tankirating.service.tracking.TrackTargetService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrackTargetSnapshotServiceImpl implements TrackTargetSnapshotService {

    private final TrackTargetService trackTargetService;
    private final TrackSnapshotRepository snapshotRepository;
    private final TrackSnapshotMapper snapshotMapper;

    @Override
    public TrackSnapshotDTO getLatestSnapshot(TrackTargetDTO target, TrackFormat format) {
        return snapshotRepository.getLatest(target.getId())
                .map(r -> snapshotMapper.toDTO(r, target, format))
                .orElseThrow(() -> snapshotNotFound(target.getName()));
    }

    @Override
    public TrackSnapshotDTO getInitSnapshot(TrackTargetDTO target, TrackFormat format) {
        return snapshotRepository.getInit(target.getId())
                .map(r -> snapshotMapper.toDTO(r, target, format))
                .orElseThrow(() -> snapshotNotFound(target.getName()));
    }

    private ExternalException snapshotNotFound(String name) {
        return new ExternalException(ExceptionType.TRACK_SNAPSHOT_NOT_FOUND)
                .arg("name", name);
    }

}
