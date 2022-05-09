package me.fizzika.tankirating.service.impl;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.AccountDTO;
import me.fizzika.tankirating.enums.ExceptionType;
import me.fizzika.tankirating.enums.track.TrackTargetType;
import me.fizzika.tankirating.exceptions.ExternalException;
import me.fizzika.tankirating.mapper.AccountMapper;
import me.fizzika.tankirating.repository.TrackTargetRepository;
import me.fizzika.tankirating.service.AccountService;
import me.fizzika.tankirating.service.tracking.TrackTargetService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final TrackTargetService trackTargetService;
    private final TrackTargetRepository trackTargetRepository;
    private final AccountMapper accountMapper;

    @Override
    public AccountDTO create(AccountDTO account) {
        // TODO: check if alternativa user is exists
        return new AccountDTO(trackTargetService.create(account.getName(), TrackTargetType.ACCOUNT).getName());
    }

    @Override
    public AccountDTO findByName(String name) {
        return trackTargetService.getByName(name, TrackTargetType.ACCOUNT)
                .map(target -> new AccountDTO(target.getName()))
                .orElseThrow(() -> new ExternalException(ExceptionType.ACCOUNT_NOT_FOUND).arg("name", name));
    }

    @Override
    public Page<AccountDTO> findAll(Pageable pageable) {
        return trackTargetRepository.findAllAccounts(pageable)
                .map(accountMapper::toAccountDTO);
    }

}
