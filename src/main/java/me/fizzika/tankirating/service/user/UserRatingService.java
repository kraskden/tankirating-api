package me.fizzika.tankirating.service.user;

import me.fizzika.tankirating.dto.rating.RatingDTO;
import me.fizzika.tankirating.dto.rating.RatingFilter;
import me.fizzika.tankirating.enums.PeriodUnit;
import org.springframework.data.domain.Pageable;

public interface UserRatingService {
    RatingDTO getRatingForPeriod(PeriodUnit period, Integer offset, RatingFilter filter, Pageable pageable);
}
