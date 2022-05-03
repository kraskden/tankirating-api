package me.fizzika.tankirating.controller;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.AccountDTO;
import me.fizzika.tankirating.service.AccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    // TODO: add recaptcha
    @PostMapping
    public AccountDTO addAccount(@Valid @RequestBody AccountDTO accountDTO) {
        return accountService.create(accountDTO);
    }

    @GetMapping("/{name}")
    public AccountDTO getAccount(@PathVariable String name) {
        return accountService.findByName(name);
    }

    @GetMapping
    public Page<AccountDTO> findAll(Pageable pageable) {
        return accountService.findAll(pageable);
    }

}
