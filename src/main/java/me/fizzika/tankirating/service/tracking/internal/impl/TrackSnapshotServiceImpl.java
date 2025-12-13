package me.fizzika.tankirating.service.tracking.internal.impl;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.mapper.TrackSnapshotMapper;
import me.fizzika.tankirating.model.TrackSnapshot;
import me.fizzika.tankirating.record.tracking.TrackSnapshotRecord;
import me.fizzika.tankirating.record.tracking.TrackTargetRecord;
import me.fizzika.tankirating.repository.tracking.TrackRepository;
import me.fizzika.tankirating.repository.tracking.TrackSnapshotRepository;
import me.fizzika.tankirating.repository.tracking.TrackTargetRepository;
import me.fizzika.tankirating.service.tracking.internal.TrackSnapshotService;
import me.fizzika.tankirating.util.Pair;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrackSnapshotServiceImpl implements TrackSnapshotService {

    private final TrackSnapshotRepository repository;
    private final TrackTargetRepository targetRepository;
    private final TrackRepository trackRepository;

    private final TrackSnapshotMapper snapshotMapper;

    @Override
    public long save(TrackSnapshot snapshot) {
        TrackTargetRecord target = targetRepository.getReferenceById(snapshot.getTargetId());
        TrackSnapshotRecord rec = snapshotMapper.toRecord(snapshot, target);
        rec = repository.save(rec);
        return rec.getId();
    }

    @Override
    public boolean exists(Integer targetId, LocalDateTime timestamp) {
        return repository.existsByTargetIdAndTimestampAndTrackRecordNotNull(targetId, timestamp);
    }

    @Override
    @Transactional
    public Optional<TrackSnapshot> findFirstInRange(Integer targetId, LocalDateTime from, LocalDateTime to) {
       return repository.findFirstSnapshotInPeriod(targetId, from, to)
               .map(snapshotMapper::toSnapshot);
    }

    @Override
    public Optional<TrackSnapshot> findLastInRange(Integer targetId, LocalDateTime from, LocalDateTime to) {
        return repository.findLastSnapshotInPeriod(targetId, from, to)
                .map(snapshotMapper::toSnapshot);
    }

    @Override
    public Optional<Pair<TrackSnapshot>> findBorderSnapshots(Integer targetId, LocalDateTime from, LocalDateTime to) {
        return repository.findBorderSnapshots(targetId, from, to)
                .map(p -> p.map(snapshotMapper::toSnapshot));
    }

    @Override
    @Transactional
    public int deleteHeadSnapshots(LocalDateTime beforeAt) {
        trackRepository.deleteHeadSnapshotTracks(beforeAt);
        return repository.deleteHeadSnapshots(beforeAt);
    }
}