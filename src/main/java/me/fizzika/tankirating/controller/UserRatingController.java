package me.fizzika.tankirating.controller;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.rating.RatingDTO;
import me.fizzika.tankirating.dto.rating.RatingFilter;
import me.fizzika.tankirating.enums.PeriodUnit;
import me.fizzika.tankirating.service.user.UserRatingService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/rating")
public class UserRatingController {

    private final UserRatingService userRatingService;

    @GetMapping("/{period}/{offset}")
    public RatingDTO getRatingForPeriod(@PathVariable PeriodUnit period,
                                        @PathVariable Integer offset,
                                        @Valid RatingFilter filter,
                                        Pageable pageable) {
        return userRatingService.getRatingForPeriod(period, offset, filter, pageable);
    }

}
