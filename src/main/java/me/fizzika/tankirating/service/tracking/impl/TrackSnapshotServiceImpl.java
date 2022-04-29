package me.fizzika.tankirating.service.tracking.impl;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.mapper.TrackDataMapper;
import me.fizzika.tankirating.mapper.TrackSnapshotMapper;
import me.fizzika.tankirating.model.TrackSnapshot;
import me.fizzika.tankirating.record.tracking.TrackSnapshotRecord;
import me.fizzika.tankirating.repository.TrackSnapshotRepository;
import me.fizzika.tankirating.service.tracking.TrackSnapshotService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrackSnapshotServiceImpl implements TrackSnapshotService {

    private final TrackSnapshotRepository repository;
    private final TrackSnapshotMapper snapshotMapper;

    @Override
    public void saveSnapshot(TrackSnapshot snapshot) {
        repository.save(snapshotMapper.toRecord(snapshot));
    }

    @Override
    public boolean existsSnapshot(UUID targetId, LocalDateTime timestamp) {
        return repository.existsByTargetIdAndTimestamp(targetId, timestamp);
    }

    @Override
    public Optional<TrackSnapshot> findClosestSnapshot(UUID targetId, LocalDateTime from, LocalDateTime to) {
       return repository.findClosestSnapshot(targetId, from, to)
               .map(snapshotMapper::toSnapshot);
    }

    @Override
    public void deleteAllSnapshots(LocalDateTime from, LocalDateTime to) {
        repository.deleteByTimestampBetween(from, to);
    }

}
