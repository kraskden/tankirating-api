package me.fizzika.tankirating.service.account.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.dto.TrackTargetDTO;
import me.fizzika.tankirating.dto.account.AccountAddDTO;
import me.fizzika.tankirating.dto.account.AccountAddResultDTO;
import me.fizzika.tankirating.dto.alternativa.track.AlternativaTrackDTO;
import me.fizzika.tankirating.dto.rating.RatingDTO;
import me.fizzika.tankirating.dto.rating.RatingFilter;
import me.fizzika.tankirating.enums.AccountAddStatus;
import me.fizzika.tankirating.enums.ExceptionType;
import me.fizzika.tankirating.enums.PeriodUnit;
import me.fizzika.tankirating.exceptions.ExternalException;
import me.fizzika.tankirating.exceptions.alternativa.AlternativaUserNotFoundException;
import me.fizzika.tankirating.mapper.AlternativaTrackingMapper;
import me.fizzika.tankirating.model.AccountData;
import me.fizzika.tankirating.model.date.DatePeriod;
import me.fizzika.tankirating.model.track_data.TrackFullData;
import me.fizzika.tankirating.record.tracking.TrackTargetRecord;
import me.fizzika.tankirating.repository.account.AccountRepository;
import me.fizzika.tankirating.repository.tracking.TrackTargetRepository;
import me.fizzika.tankirating.service.account.AccountService;
import me.fizzika.tankirating.service.tracking.TrackTargetService;
import me.fizzika.tankirating.service.tracking.internal.AlternativaTrackingService;
import me.fizzika.tankirating.service.tracking.internal.TrackStoreService;
import me.fizzika.tankirating.service.tracking.internal.TrackingUpdateService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

import static me.fizzika.tankirating.enums.ExceptionType.TRACK_TARGET_NOT_FOUND;
import static me.fizzika.tankirating.enums.track.TrackTargetType.ACCOUNT;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TrackTargetRepository trackTargetRepository;
    private final TrackTargetService trackTargetService;

    private final AlternativaTrackingService alternativaTrackingService;
    private final TrackStoreService trackStoreService;
    private final TrackingUpdateService trackingUpdateService;

    private final AlternativaTrackingMapper trackingMapper;

    @Override
    public RatingDTO getRatingForPeriod(PeriodUnit period, Integer offset, RatingFilter filter, Pageable pageable) {
        DatePeriod datePeriod = period.getDatePeriod(LocalDateTime.now()).sub(offset);
        return new RatingDTO(period, datePeriod.getStart(), datePeriod.getEnd(),
                accountRepository.getRating(period, datePeriod.getStart(), filter.getMinScore(), pageable));
    }

    @Override
    public List<AccountAddResultDTO> addAccounts(AccountAddDTO addDTO) {
        Set<String> existing = accountRepository.existingNicknamesInLowerCase(addDTO.getNicknames().stream()
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
        return trackingUpdateService.updateOne(trackTargetDTO).getAccount();
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
