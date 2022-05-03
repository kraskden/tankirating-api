package me.fizzika.tankirating.service.impl;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.AccountDTO;
import me.fizzika.tankirating.enums.ExceptionType;
import me.fizzika.tankirating.exceptions.ExternalException;
import me.fizzika.tankirating.mapper.AccountMapper;
import me.fizzika.tankirating.record.tracking.TrackTargetRecord;
import me.fizzika.tankirating.repository.TrackTargetRepository;
import me.fizzika.tankirating.service.AccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final TrackTargetRepository trackTargetRepository;
    private final AccountMapper accountMapper;

    @Override
    public AccountDTO create(AccountDTO account) {
        // TODO: check if alternativa user is exists
        if (trackTargetRepository.existsByNameIgnoreCase(account.getName())) {
            throw new ExternalException(ExceptionType.ACCOUNT_ALREADY_EXISTS)
                    .arg("name", account.getName());
        }
        TrackTargetRecord saved = trackTargetRepository.save(accountMapper.toRecord(account));
        return accountMapper.toAccountDTO(saved);
    }

    @Override
    public AccountDTO findByName(String name) {
        if (name.startsWith("~")) {
            throw new ExternalException(ExceptionType.ACCOUNT_NOT_FOUND).arg("name", name);
        }
        return trackTargetRepository.findByNameIgnoreCase(name)
                .map(accountMapper::toAccountDTO)
                .orElseThrow(() -> new ExternalException(ExceptionType.ACCOUNT_NOT_FOUND).arg("name", name));
    }

    @Override
    public Page<AccountDTO> findAll(Pageable pageable) {
        return trackTargetRepository.findAllAccounts(pageable)
                .map(accountMapper::toAccountDTO);
    }

}
