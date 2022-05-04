package me.fizzika.tankirating.service.tracking.impl;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.mapper.TrackSnapshotMapper;
import me.fizzika.tankirating.model.TrackSnapshot;
import me.fizzika.tankirating.repository.TrackSnapshotRepository;
import me.fizzika.tankirating.service.tracking.TrackSnapshotService;
import me.fizzika.tankirating.util.Pair;
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
    @Transactional
    public Optional<TrackSnapshot> findClosestSnapshot(UUID targetId, LocalDateTime from, LocalDateTime to) {
       return repository.findClosestSnapshot(targetId, from, to)
               .map(snapshotMapper::toSnapshot);
    }

    @Override
    public Optional<Pair<TrackSnapshot>> findBorderSnapshots(UUID targetId, LocalDateTime from, LocalDateTime to) {
        return repository.findBorderSnapshots(targetId, from, to)
                .map(p -> p.map(snapshotMapper::toSnapshot));
    }

    @Override
    public void deleteAllSnapshots(LocalDateTime from, LocalDateTime to) {
        repository.deleteByTimestampBetween(from, to);
    }

}
