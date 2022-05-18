package me.fizzika.tankirating.controller;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.filter.DatesFilter;
import me.fizzika.tankirating.dto.tracking.TrackHeatMapDTO;
import me.fizzika.tankirating.service.tracking.TrackHeatMapService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/target/{targetId}/heatmap")
public class TrackHeatmapController {

    private final TrackHeatMapService trackHeatMapService;

    @GetMapping("/time")
    public List<TrackHeatMapDTO> getHeatMap(@PathVariable Integer targetId, @Valid DatesFilter datesFilter) {
        return trackHeatMapService.getHeatMap(targetId, datesFilter);
    }

}
