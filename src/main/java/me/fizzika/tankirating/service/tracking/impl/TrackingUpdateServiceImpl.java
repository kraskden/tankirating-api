package me.fizzika.tankirating.service.tracking.impl;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.mapper.TrackRecordMapper;
import me.fizzika.tankirating.service.tracking.AlternativaTrackingService;
import me.fizzika.tankirating.service.tracking.TrackingUpdateService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrackingUpdateServiceImpl implements TrackingUpdateService {

    private final AlternativaTrackingService alternativaTrackingService;
    private final TrackRecordMapper recordMapper;

    @Override
    public void updateAccount(String nickname) {

    }

    @Override
    public void updateAll() {

    }

}
