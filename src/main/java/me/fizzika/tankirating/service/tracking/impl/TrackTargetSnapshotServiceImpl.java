package me.fizzika.tankirating.service.tracking.impl;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.tracking.TrackSnapshotDTO;
import me.fizzika.tankirating.enums.ExceptionType;
import me.fizzika.tankirating.enums.track.TrackFormat;
import me.fizzika.tankirating.exceptions.ExternalException;
import me.fizzika.tankirating.mapper.TrackSnapshotMapper;
import me.fizzika.tankirating.repository.tracking.TrackSnapshotRepository;
import me.fizzika.tankirating.service.tracking.TrackTargetSnapshotService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TrackTargetSnapshotServiceImpl implements TrackTargetSnapshotService {

    private final TrackSnapshotRepository snapshotRepository;
    private final TrackSnapshotMapper snapshotMapper;

    @Override
    public TrackSnapshotDTO getLatestSnapshot(Integer targetId, TrackFormat format) {
        return snapshotRepository.getLatest(targetId)
                .map(r -> snapshotMapper.toDTO(r, targetId, format))
                .orElseThrow(() -> snapshotNotFound(targetId));
    }

    @Override
    public TrackSnapshotDTO getInitSnapshot(Integer targetId, TrackFormat format) {
        return snapshotRepository.getInit(targetId)
                .map(r -> snapshotMapper.toDTO(r, targetId, format))
                .orElseThrow(() -> snapshotNotFound(targetId));
    }

    @Override
    public TrackSnapshotDTO getSnapshotForDate(Integer targetId, LocalDate date, TrackFormat format) {
        return snapshotRepository.findByDate(targetId, date)
                .map(r -> snapshotMapper.toDTO(r, targetId, format))
                .orElseThrow(() -> snapshotNotFound(targetId));
    }

    private ExternalException snapshotNotFound(Integer targetId) {
        return new ExternalException(ExceptionType.TRACK_SNAPSHOT_NOT_FOUND)
                .arg("targetId", targetId);
    }

}
