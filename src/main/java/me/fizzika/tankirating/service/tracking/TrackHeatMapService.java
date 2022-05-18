package me.fizzika.tankirating.service.tracking;

import me.fizzika.tankirating.dto.filter.DatesFilter;
import me.fizzika.tankirating.dto.tracking.TrackHeatMapDTO;

import java.util.List;

public interface TrackHeatMapService {

    List<TrackHeatMapDTO> getHeatMap(Integer targetId, DatesFilter datesFilter);

}
