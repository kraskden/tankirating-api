package me.fizzika.tankirating.service.tracking.impl;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.filter.DatesFilter;
import me.fizzika.tankirating.dto.tracking.TrackHeatMapDTO;
import me.fizzika.tankirating.repository.tracking.TrackDiffRepository;
import me.fizzika.tankirating.service.tracking.TrackHeatMapService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackHeatMapServiceImpl implements TrackHeatMapService {

    private final TrackDiffRepository diffRepository;

    @Override
    public List<TrackHeatMapDTO> getHeatMap(Integer targetId, DatesFilter datesFilter) {
        return diffRepository.getHeatMap(targetId, datesFilter.getFrom().atStartOfDay(),
                datesFilter.getTo().atStartOfDay(), Sort.by("periodStart"));
    }

}
