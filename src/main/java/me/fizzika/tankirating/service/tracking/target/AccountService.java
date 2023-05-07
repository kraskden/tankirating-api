package me.fizzika.tankirating.service.tracking.target;

import me.fizzika.tankirating.dto.target.TrackTargetDTO;
import me.fizzika.tankirating.dto.target.AccountAddDTO;
import me.fizzika.tankirating.dto.target.AccountAddResultDTO;
import me.fizzika.tankirating.dto.rating.RatingDTO;
import me.fizzika.tankirating.dto.rating.RatingFilter;
import me.fizzika.tankirating.enums.PeriodUnit;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AccountService {
    RatingDTO getRatingForPeriod(PeriodUnit period, Integer offset, RatingFilter filter, Pageable pageable);

    List<AccountAddResultDTO> addAccounts(AccountAddDTO addDTO);

    TrackTargetDTO activate(Integer id);
}
