package me.fizzika.tankirating.controller;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.account.AccountAddDTO;
import me.fizzika.tankirating.dto.account.AccountAddResultDTO;
import me.fizzika.tankirating.dto.rating.RatingDTO;
import me.fizzika.tankirating.dto.rating.RatingFilter;
import me.fizzika.tankirating.enums.PeriodUnit;
import me.fizzika.tankirating.service.account.AccountService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/rating/{period}/{offset}")
    public RatingDTO getRatingForPeriod(@PathVariable PeriodUnit period,
                                        @PathVariable Integer offset,
                                        @Valid RatingFilter filter,
                                        Pageable pageable) {
        return accountService.getRatingForPeriod(period, offset, filter, pageable);
    }

    @PostMapping
    public List<AccountAddResultDTO> addUsers(@RequestBody @Valid AccountAddDTO addDTO) {
        return accountService.addAccounts(addDTO);
    }

}
