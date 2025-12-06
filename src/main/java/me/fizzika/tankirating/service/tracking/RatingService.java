package me.fizzika.tankirating.service.tracking;

import me.fizzika.tankirating.dto.rating.RatingDTO;
import me.fizzika.tankirating.dto.rating.RatingFilter;
import me.fizzika.tankirating.enums.DiffPeriodUnit;
import org.springframework.data.domain.Pageable;

public interface RatingService {
    RatingDTO getRatingForPeriod(DiffPeriodUnit period, Integer offset, RatingFilter filter, Pageable pageable);
    void resetCache();
}