package me.fizzika.tankirating.service.account;

import me.fizzika.tankirating.dto.TrackTargetDTO;
import me.fizzika.tankirating.dto.account.AccountAddDTO;
import me.fizzika.tankirating.dto.account.AccountAddResultDTO;
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
