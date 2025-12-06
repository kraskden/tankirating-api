package me.fizzika.tankirating.service.tracking.target;

import me.fizzika.tankirating.dto.target.AccountUpdateResultDTO;
import me.fizzika.tankirating.dto.target.TrackTargetDTO;
import me.fizzika.tankirating.dto.target.AccountAddDTO;
import me.fizzika.tankirating.dto.target.AccountAddResultDTO;

import java.util.List;

public interface AccountService {
    List<AccountAddResultDTO> addAccounts(AccountAddDTO addDTO);

    TrackTargetDTO activate(Integer id);

    AccountUpdateResultDTO update(Integer id);
}