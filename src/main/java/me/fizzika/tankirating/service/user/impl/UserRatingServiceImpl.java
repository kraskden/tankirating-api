package me.fizzika.tankirating.service.user.impl;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.rating.RatingDTO;
import me.fizzika.tankirating.enums.PeriodUnit;
import me.fizzika.tankirating.model.DatePeriod;
import me.fizzika.tankirating.repository.user.UserRepository;
import me.fizzika.tankirating.service.user.UserRatingService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserRatingServiceImpl implements UserRatingService {

    private final UserRepository userRepository;

    @Override
    public RatingDTO getRatingForPeriod(PeriodUnit period, Integer offset, Pageable pageable) {
        DatePeriod datePeriod = period.getDatePeriod(LocalDateTime.now()).sub(offset);
        return new RatingDTO(period, datePeriod.getStart(), datePeriod.getEnd(),
                userRepository.getRating(period, datePeriod.getStart(), pageable));
    }

}
