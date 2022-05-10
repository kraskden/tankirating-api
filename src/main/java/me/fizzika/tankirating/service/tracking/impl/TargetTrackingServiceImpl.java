package me.fizzika.tankirating.service.tracking.impl;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.model.track_data.TrackFullData;
import me.fizzika.tankirating.service.tracking.TargetTrackingService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class TargetTrackingServiceImpl implements TargetTrackingService {

    @Override
    @Transactional
    public void updateTargetData(Integer targetId, TrackFullData currentData) {

    }

}
