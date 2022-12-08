package me.fizzika.tankirating.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.filter.TrackDatesFilter;
import me.fizzika.tankirating.dto.filter.TrackDiffFilter;
import me.fizzika.tankirating.dto.filter.TrackFormatFilter;
import me.fizzika.tankirating.dto.tracking.TrackDiffDTO;
import me.fizzika.tankirating.enums.PeriodUnit;
import me.fizzika.tankirating.service.tracking.TrackDiffService;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/target/{targetId}/diff")
@Tag(name = "Diff statistics", description = "Provide game stat for time interval or interval series")
public class TrackDiffController {

    private final TrackDiffService trackDiffService;

    @GetMapping("/custom")
    public TrackDiffDTO getDiffForDates(@PathVariable Integer targetId,
                                        @ParameterObject @Valid TrackDatesFilter datesFilter) {
        return trackDiffService.calculateDiffBetweenDates(targetId, datesFilter);
    }

    @GetMapping("/{period}")
    public List<TrackDiffDTO> getAllDiffsForPeriod(@PathVariable Integer targetId,
                                                   @PathVariable PeriodUnit period,
                                                   @ParameterObject @Valid TrackDiffFilter diffFilter) {
        return trackDiffService.getAllDiffsForPeriod(targetId, period, diffFilter);
    }

    @GetMapping("/{period}/{offset}")
    public TrackDiffDTO getDiffForPeriod(@PathVariable Integer targetId,
                                         @PathVariable PeriodUnit period,
                                         @PathVariable Integer offset,
                                         @ParameterObject @Valid TrackFormatFilter formatFilter) {
        return trackDiffService.getDiffForPeriod(targetId, period, offset, formatFilter.getFormat());
    }

}
