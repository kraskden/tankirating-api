package me.fizzika.tankirating.service.tracking;

import java.time.LocalDateTime;
import me.fizzika.tankirating.dto.rating.AccountRatingDTO;
import me.fizzika.tankirating.dto.rating.RatingFilter;
import me.fizzika.tankirating.enums.PeriodUnit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RatingService {

    Page<AccountRatingDTO> getAccountRating(PeriodUnit period, Integer offset, RatingFilter filter, Pageable pageable);
}