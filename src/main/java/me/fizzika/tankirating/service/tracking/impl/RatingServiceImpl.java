package me.fizzika.tankirating.service.tracking.impl;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.rating.AccountRatingDTO;
import me.fizzika.tankirating.dto.rating.RatingDTO;
import me.fizzika.tankirating.dto.rating.RatingFilter;
import me.fizzika.tankirating.enums.PeriodUnit;
import me.fizzika.tankirating.model.RatingParams;
import me.fizzika.tankirating.model.date.DatePeriod;
import me.fizzika.tankirating.repository.tracking.TrackTargetRepository;
import me.fizzika.tankirating.service.tracking.RatingService;
import me.fizzika.tankirating.util.Cache;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private static final Duration CACHE_EXPIRATION = Duration.ofMinutes(15);

    private final Cache<RatingParams, Page<AccountRatingDTO>> cache = new Cache<>(CACHE_EXPIRATION);

    private final TrackTargetRepository trackTargetRepository;

    @Override
    public RatingDTO getRatingForPeriod(PeriodUnit period, Integer offset, RatingFilter filter, Pageable pageable) {
        RatingParams ratingParams = new RatingParams(period, offset, filter, pageable);
        DatePeriod datePeriod = ratingParams.getDatePeriod();

        Page<AccountRatingDTO> ratingPage = getAccountRating(ratingParams);
        return new RatingDTO(period, datePeriod.getStart(), datePeriod.getEnd(), ratingPage);
    }

    @Override
    public void resetCache() {
        cache.clearAll();
    }

    public Page<AccountRatingDTO> getAccountRating(RatingParams ratingParams) {
        if (ratingParams.isCacheable()) {
            return cache.getOrCompute(ratingParams, this::calcAccountRating);
        }
        return calcAccountRating(ratingParams);
    }

    private Page<AccountRatingDTO> calcAccountRating(RatingParams ratingParams) {
        DatePeriod datePeriod = ratingParams.getDatePeriod();
        RatingFilter filter = ratingParams.filter();

        return filter.getIds() == null ?
                trackTargetRepository.getAccountRating(ratingParams.period(), datePeriod.getStart(), filter.getMinScore(), ratingParams.pageable()) :
                trackTargetRepository.getAccountRating(ratingParams.period(), datePeriod.getStart(), filter.getMinScore(), filter.getIds(), ratingParams.pageable());
    }
}