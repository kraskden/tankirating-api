package me.fizzika.tankirating.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.target.TrackTargetDTO;
import me.fizzika.tankirating.dto.target.AccountAddDTO;
import me.fizzika.tankirating.dto.target.AccountAddResultDTO;
import me.fizzika.tankirating.dto.rating.RatingDTO;
import me.fizzika.tankirating.dto.rating.RatingFilter;
import me.fizzika.tankirating.enums.PeriodUnit;
import me.fizzika.tankirating.service.tracking.target.AccountService;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
@Tag(name = "Account", description = "API for account manipulation")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/rating/{period}/{offset}")
    public RatingDTO getRatingForPeriod(@PathVariable PeriodUnit period,
                                        @PathVariable Integer offset,
                                        @ParameterObject @Valid RatingFilter filter,
                                        @ParameterObject Pageable pageable) {
        return accountService.getRatingForPeriod(period, offset, filter, pageable);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CAPTCHA')")
    public List<AccountAddResultDTO> addUsers(@RequestBody @Valid AccountAddDTO addDTO) {
        return accountService.addAccounts(addDTO);
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('CAPTCHA')")
    public TrackTargetDTO activate(@PathVariable Integer id) {
        return accountService.activate(id);
    }

}
