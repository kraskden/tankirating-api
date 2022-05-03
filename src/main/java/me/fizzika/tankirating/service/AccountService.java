package me.fizzika.tankirating.service;

import me.fizzika.tankirating.dto.AccountDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountService {

    AccountDTO create(AccountDTO account);

    AccountDTO findByName(String name);

    Page<AccountDTO> findAll(Pageable pageable);

}
