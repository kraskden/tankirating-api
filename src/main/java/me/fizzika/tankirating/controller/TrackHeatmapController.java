package me.fizzika.tankirating.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.filter.DatesFilter;
import me.fizzika.tankirating.dto.tracking.TrackHeatMapDTO;
import me.fizzika.tankirating.service.tracking.TrackHeatMapService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/target/{targetId}/heatmap")
@Tag(name = "Heatmap", description = "API for github-like chart")
public class TrackHeatmapController {

    private final TrackHeatMapService trackHeatMapService;

    @GetMapping
    public List<TrackHeatMapDTO> getHeatMap(@PathVariable Integer targetId,
                                            @ParameterObject @Valid DatesFilter datesFilter) {
        return trackHeatMapService.getHeatMap(targetId, datesFilter);
    }

}