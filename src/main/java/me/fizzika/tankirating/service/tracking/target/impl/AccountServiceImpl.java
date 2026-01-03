package me.fizzika.tankirating.service.tracking.target.impl;

import static me.fizzika.tankirating.enums.ExceptionType.TRACK_TARGET_NOT_FOUND;
import static me.fizzika.tankirating.enums.track.TrackTargetType.ACCOUNT;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.dto.alternativa.track.AlternativaTrackDTO;
import me.fizzika.tankirating.dto.target.AccountAddDTO;
import me.fizzika.tankirating.dto.target.AccountAddResultDTO;
import me.fizzika.tankirating.dto.target.AccountUpdateResultDTO;
import me.fizzika.tankirating.dto.target.TrackTargetDTO;
import me.fizzika.tankirating.enums.AccountAddStatus;
import me.fizzika.tankirating.exceptions.ExternalException;
import me.fizzika.tankirating.exceptions.alternativa.AlternativaUserNotFoundException;
import me.fizzika.tankirating.mapper.AlternativaTrackingMapper;
import me.fizzika.tankirating.mapper.TrackTargetMapper;
import me.fizzika.tankirating.model.AccountData;
import me.fizzika.tankirating.model.AccountUpdateResult;
import me.fizzika.tankirating.model.track_data.TrackFullData;
import me.fizzika.tankirating.record.tracking.TrackTargetRecord;
import me.fizzika.tankirating.repository.tracking.TrackTargetRepository;
import me.fizzika.tankirating.service.tracking.internal.AlternativaTrackingService;
import me.fizzika.tankirating.service.tracking.internal.TrackStoreService;
import me.fizzika.tankirating.service.tracking.target.AccountService;
import me.fizzika.tankirating.service.tracking.target.TrackTargetService;
import me.fizzika.tankirating.service.tracking.update.TrackingUpdateService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final TrackTargetRepository trackTargetRepository;
    private final TrackTargetService trackTargetService;
    private final TrackTargetMapper trackTargetMapper;

    private final AlternativaTrackingService alternativaTrackingService;
    private final TrackStoreService trackStoreService;
    private final TrackingUpdateService trackingUpdateService;

    private final AlternativaTrackingMapper trackingMapper;

    @Override
    public List<AccountAddResultDTO> addAccounts(AccountAddDTO addDTO) {
        Set<String> existing = trackTargetRepository.existingNicknamesInLowerCase(addDTO.getNicknames().stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList()));

        return addDTO.getNicknames().stream().parallel()
                .map(nickname -> new AccountAddResultDTO(nickname, addAccount(nickname, existing)))
                .collect(Collectors.toList());
    }

    @Override
    public TrackTargetDTO activate(Integer id) {
        TrackTargetDTO trackTargetDTO = trackTargetService.getOptionalById(id, ACCOUNT)
                .orElseThrow(() -> new ExternalException(TRACK_TARGET_NOT_FOUND));
        return trackingUpdateService.updateAccount(trackTargetDTO).getAccount();
    }

    @Override
    public AccountUpdateResultDTO update(Integer id) {
        TrackTargetDTO trackTargetDTO = trackTargetService.getOptionalById(id, ACCOUNT)
                .orElseThrow(() -> new ExternalException(TRACK_TARGET_NOT_FOUND));
        AccountUpdateResult updateResult = trackingUpdateService.updateAccount(trackTargetDTO);
        log.info("Account {} has been manually updated", trackTargetDTO.getName());
        return trackTargetMapper.toDto(updateResult);
    }

    private AccountAddStatus addAccount(String nickname, Set<String> existing) {
        if (existing.contains(nickname.toLowerCase())) {
            return AccountAddStatus.ALREADY_EXISTS;
        }
        AlternativaTrackDTO track = null;
        try {
            track = alternativaTrackingService.getTracking(nickname).join();
        } catch (CompletionException ex) {
            if (ex.getCause() instanceof AlternativaUserNotFoundException) {
                return AccountAddStatus.NOT_FOUND;
            }
            log.error("Unknown error during account {} adding", nickname, ex.getCause());
            return AccountAddStatus.UNKNOWN_ERROR;
        }
        TrackTargetRecord rec = new TrackTargetRecord(nickname, ACCOUNT);
        rec = trackTargetRepository.save(rec);

        AccountData<TrackFullData> accountData = trackingMapper.toFullAccountData(track);
        trackStoreService.updateTargetData(rec.getId(), accountData.getTrackData(), accountData.isHasPremium());
        log.info("Registered account {}", rec.getName());
        return AccountAddStatus.OK;
    }
}