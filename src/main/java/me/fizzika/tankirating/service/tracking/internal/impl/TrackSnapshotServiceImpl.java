package me.fizzika.tankirating.service.tracking.internal.impl;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.mapper.TrackSnapshotMapper;
import me.fizzika.tankirating.model.TrackSnapshot;
import me.fizzika.tankirating.repository.tracking.TrackSnapshotRepository;
import me.fizzika.tankirating.service.tracking.internal.TrackSnapshotService;
import me.fizzika.tankirating.util.Pair;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrackSnapshotServiceImpl implements TrackSnapshotService {

    private final TrackSnapshotRepository repository;
    private final TrackSnapshotMapper snapshotMapper;

    @Override
    public long save(TrackSnapshot snapshot) {
        var rec = repository.save(snapshotMapper.toRecord(snapshot));
        return rec.getId();
    }

    @Override
    public boolean exists(Integer targetId, LocalDateTime timestamp) {
        return repository.existsByTargetIdAndTimestamp(targetId, timestamp);
    }

    @Override
    @Transactional
    public Optional<TrackSnapshot> findFirstInRange(Integer targetId, LocalDateTime from, LocalDateTime to) {
       return repository.findFirstSnapshot(targetId, from, to)
               .map(snapshotMapper::toSnapshot);
    }

    @Override
    public Optional<TrackSnapshot> findLastInRange(Integer targetId, LocalDateTime from, LocalDateTime to) {
        return repository.findLastSnapshot(targetId, from, to)
                .map(snapshotMapper::toSnapshot);
    }

    @Override
    public Optional<Pair<TrackSnapshot>> findBorderSnapshots(Integer targetId, LocalDateTime from, LocalDateTime to) {
        return repository.findBorderSnapshots(targetId, from, to)
                .map(p -> p.map(snapshotMapper::toSnapshot));
    }

    @Override
    public void deleteAllInRange(LocalDateTime from, LocalDateTime to) {
        repository.deleteByTimestampBetween(from, to);
    }

}
