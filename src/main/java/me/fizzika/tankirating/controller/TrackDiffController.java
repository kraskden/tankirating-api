package me.fizzika.tankirating.controller;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.TrackTargetDTO;
import me.fizzika.tankirating.dto.filter.TrackDatesFilter;
import me.fizzika.tankirating.dto.filter.TrackFormatFilter;
import me.fizzika.tankirating.dto.tracking.TrackDiffDTO;
import me.fizzika.tankirating.enums.PeriodUnit;
import me.fizzika.tankirating.enums.track.TrackTargetType;
import me.fizzika.tankirating.service.tracking.TrackDiffService;
import me.fizzika.tankirating.service.tracking.TrackTargetService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/target/{targetId}/diff")
public class TrackDiffController {

    private final TrackDiffService trackDiffService;

    @GetMapping("/custom")
    public TrackDiffDTO getDiffForDates(@PathVariable Integer targetId, @Valid TrackDatesFilter datesFilter) {
        return trackDiffService.calculateDiffBetweenDates(targetId, datesFilter);
    }

    @GetMapping("/{period}")
    public List<TrackDiffDTO> getAllDiffsForPeriod(@PathVariable Integer targetId, @PathVariable PeriodUnit period,
        @Valid TrackDatesFilter datesFilter) {
        return trackDiffService.getAllDiffsForPeriod(targetId, period, datesFilter);
    }

    @GetMapping("/{period}/{offset}")
    public TrackDiffDTO getDiffForPeriod(@PathVariable Integer targetId, @PathVariable PeriodUnit period,
                                         @PathVariable Integer offset, @Valid TrackFormatFilter formatFilter) {
        return trackDiffService.getDiffForPeriod(targetId, period, offset, formatFilter.getFormat());
    }

}
