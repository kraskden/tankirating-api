package me.fizzika.tankirating.service.management.impl;

import java.util.List;
import jakarta.annotation.Resource;
import me.fizzika.tankirating.dto.management.TargetStatEntryDTO;
import me.fizzika.tankirating.repository.tracking.TrackTargetRepository;
import me.fizzika.tankirating.service.management.StatService;
import org.springframework.stereotype.Service;

@Service
public class StatServiceImpl implements StatService {

    @Resource
    private TrackTargetRepository targetRepository;

    @Override
    public List<TargetStatEntryDTO> getTargetStat() {
        return targetRepository.getTargetStat();
    }
}