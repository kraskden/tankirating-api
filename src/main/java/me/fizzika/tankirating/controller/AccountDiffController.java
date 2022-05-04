package me.fizzika.tankirating.controller;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.filter.TrackFormatFilter;
import me.fizzika.tankirating.dto.filter.TrackDatesFilter;
import me.fizzika.tankirating.dto.tracking.TrackDiffDTO;
import me.fizzika.tankirating.enums.track.TrackDiffPeriod;
import me.fizzika.tankirating.service.AccountDiffService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account/{nickname}/diff")
public class AccountDiffController {

    private final AccountDiffService accountDiffService;

    @GetMapping("/custom")
    public TrackDiffDTO getDiffForDates(@PathVariable String nickname, @Valid TrackDatesFilter datesFilter) {
        return accountDiffService.calculateDiffBetweenDates(nickname, datesFilter);
    }

    @GetMapping("/allTime")
    public TrackDiffDTO getAllTimeDiff(@PathVariable String nickname, @Valid TrackFormatFilter formatFilter) {
        return accountDiffService.getAllTimeDiff(nickname, formatFilter.getFormat());
    }

    @GetMapping("/{period}")
    public List<TrackDiffDTO> getAllDiffsForPeriod(@PathVariable String nickname, @PathVariable TrackDiffPeriod period,
        @Valid TrackDatesFilter datesFilter) {
        return accountDiffService.getAllDiffsForPeriod(nickname, period, datesFilter);
    }

    @GetMapping("/{period}/{offset}")
    public TrackDiffDTO getDiffForPeriod(@PathVariable String nickname, @PathVariable TrackDiffPeriod period,
                                                 @PathVariable Integer offset, @Valid TrackFormatFilter formatFilter) {
        return accountDiffService.getDiffForPeriod(nickname, period, offset, formatFilter.getFormat());
    }

}
