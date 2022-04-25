package me.fizzika.tankirating.service.tracking.impl;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.enums.TrackTargetType;
import me.fizzika.tankirating.mapper.AlternativaTrackingMapper;
import me.fizzika.tankirating.mapper.TrackModelMapper;
import me.fizzika.tankirating.model.tracking.TrackFullModel;
import me.fizzika.tankirating.record.tracking.TrackRecord;
import me.fizzika.tankirating.record.tracking.TrackSnapshotRecord;
import me.fizzika.tankirating.repository.TrackSnapshotRepository;
import me.fizzika.tankirating.service.tracking.AlternativaTrackingService;
import me.fizzika.tankirating.service.tracking.TrackTargetService;
import me.fizzika.tankirating.service.tracking.TrackingUpdateService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrackingUpdateServiceImpl implements TrackingUpdateService {

    private final TrackTargetService targetService;
    private final AlternativaTrackingService alternativaService;

    private final AlternativaTrackingMapper alternativaMapper;
    private final TrackModelMapper trackModelMapper;

    private final TrackSnapshotRepository snapshotRepository;

    @Override
    public void updateAccount(UUID id, String nickname) {
        // TODO: N+1 problem
        TrackFullModel current = alternativaMapper.toFullTrackModel(alternativaService.getTracking(nickname));
        TrackRecord record = trackModelMapper.toTrackRecord(current);
        record.getActivities().forEach(a -> a.setTrack(record));
        record.getSupplies().forEach(s -> s.setTrack(record));

        TrackSnapshotRecord snapshotRecord = new TrackSnapshotRecord();
        snapshotRecord.setTargetId(id);
        snapshotRecord.setTimestamp(LocalDateTime.now());
        snapshotRecord.setTrackData(record);

        snapshotRepository.save(snapshotRecord);
    }

    @Override
    @PostConstruct
    public void updateAll() {
        targetService.getAllTargets().stream()
                .filter(t -> t.getType() == TrackTargetType.ACCOUNT)
                .forEach(t -> updateAccount(t.getId(), t.getName()));
    }


}
