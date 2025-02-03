package me.fizzika.tankirating.service.tracking.internal.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.dto.target.TrackTargetDTO;
import me.fizzika.tankirating.dto.filter.TrackTargetFilter;
import me.fizzika.tankirating.enums.track.TrackTargetStatus;
import me.fizzika.tankirating.enums.track.TrackTargetType;
import me.fizzika.tankirating.exceptions.alternativa.AlternativaException;
import me.fizzika.tankirating.exceptions.alternativa.AlternativaTooManyRequestsException;
import me.fizzika.tankirating.exceptions.alternativa.AlternativaUserNotFoundException;
import me.fizzika.tankirating.exceptions.tracking.InvalidTrackDataException;
import me.fizzika.tankirating.mapper.AlternativaTrackingMapper;
import me.fizzika.tankirating.model.AccountUpdateResult;
import me.fizzika.tankirating.model.AccountsUpdateStat;
import me.fizzika.tankirating.model.TrackGroup;
import me.fizzika.tankirating.service.tracking.target.TrackTargetService;
import me.fizzika.tankirating.service.tracking.internal.AlternativaTrackingService;
import me.fizzika.tankirating.service.tracking.internal.TrackStoreService;
import me.fizzika.tankirating.service.tracking.internal.TrackingUpdateService;
import me.fizzika.tankirating.util.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static me.fizzika.tankirating.enums.track.TrackTargetStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackingUpdateServiceImpl implements TrackingUpdateService {

    @Value("${app.tracking.update-buffer-size}")
    private int accountBufferSize;

    @Value("${app.tracking.update-buffer-timeout}")
    private Duration accountBufferTimeout;

    @Value("${app.tracking.max-retries}")
    private int maxRetries;

    @Value("${app.tracking.retry-timeout-per-account}")
    private Duration retryTimeoutPerAccount;

    private final AlternativaTrackingService alternativaService;
    private final AlternativaTrackingMapper alternativaMapper;

    private final TrackTargetService targetService;
    private final TrackStoreService trackStoreService;

    private final Map<TrackTargetStatus, Lock> lockMap = new EnumMap<>(TrackTargetStatus.class);

    @Override
    public AccountUpdateResult updateOne(TrackTargetDTO account) {
        return updateAccountAsync(account).join();
    }

    @Override
    public void updateAll(TrackTargetStatus targetStatus) {
        lockMap.putIfAbsent(targetStatus, new ReentrantLock());
        Lock lock = lockMap.get(targetStatus);
        if (!lock.tryLock()) {
            log.error("Update {} is running, skipping and exiting", targetStatus);
        }
        try {
            List<TrackTargetDTO> accounts = getAccounts(Set.of(targetStatus));
            log.info("Start updating {} {} accounts", accounts.size(), targetStatus);
            doUpdate(accounts);
            log.info("Finish updating {} {} accounts", accounts.size(), targetStatus);
        } finally {
            lock.unlock();
        }
    }

    private void doUpdate(List<TrackTargetDTO> accounts) {
        log.info("Updating parameters: buffer={}, timeout={}s", accountBufferSize,
                accountBufferTimeout.toSeconds());

        try {
            alternativaService.healthCheck().join();
        } catch (CompletionException ex) {
            log.error("Alternativa service is unavailable ", ex.getCause());
            log.error("Skipping accounts updating");
            return;
        }

        long total = accounts.size();
        long processed = 0;

        log.info("Found {} accounts", total);

        for (List<TrackTargetDTO> slice : Utils.split(accounts, accountBufferSize)) {
            updateAccounts(slice);
            processed += slice.size();
            log.info("[{}/{}] Processed", processed, total);
            if (processed != total) {
                log.info("Sleeping for {}", accountBufferTimeout);
                sleep(accountBufferTimeout);
            }
        }

        log.info("All accounts has been processed");
        sleep(accountBufferTimeout);

        log.info("Groups update has been started");
        List<TrackGroup> groups = targetService.getAllGroups();
        log.info("Found {} groups: {}", groups.size(), groups);
        groups.forEach(trackStoreService::updateCurrentGroupData);
        log.info("Groups update finished");
    }

    private void updateAccounts(Collection<TrackTargetDTO> accounts) {
        AccountsUpdateStat stat = updateAccounts(accounts, 0);
        log.info("[SLICE] Processed [{}/{}], Failed: {}", stat.getProcessedCount(),
                stat.getTotalCount(), stat.getRetriedCount());
        stat.getRetried().forEach(a -> updateAccountStatusIfNeed(a, FROZEN));
    }

    private AccountsUpdateStat updateAccounts(Collection<TrackTargetDTO> accounts, int retry) {
        var stat = updateAccountsAsync(accounts).join();
        log.info("[SLICE] Processed [{}/{}], Retried: {}", stat.getProcessedCount(), stat.getTotalCount(), stat.getRetriedCount());
        if (stat.getRetriedCount() != 0 && retry < maxRetries) {
            var sleepDuration = retryTimeoutPerAccount.multipliedBy(stat.getRetriedCount());
            log.info("Waiting for retry, sleep for {}", sleepDuration);
            sleep(sleepDuration);
            var retriedStat = updateAccounts(stat.getRetried(), retry + 1);
            stat.setRetried(retriedStat.getRetried());
            stat.setProcessedCount(stat.getProcessedCount() + retriedStat.getProcessedCount());
        }
        return stat;
    }

    private CompletableFuture<AccountsUpdateStat> updateAccountsAsync(Collection<TrackTargetDTO> accounts) {
        List<CompletableFuture<AccountUpdateResult>> updaters = accounts.stream()
                .map(this::updateAccountAsync)
                .collect(Collectors.toList());

        return CompletableFuture.allOf(updaters.toArray(CompletableFuture[]::new))
                .thenApply(ignored -> {
                    AccountsUpdateStat stat = new AccountsUpdateStat();
                    stat.setTotalCount(accounts.size());
                    for (var updater : updaters) {
                        AccountUpdateResult updateResult = updater.join();
                        if (updateResult.isProcessed()) {
                            stat.setProcessedCount(stat.getProcessedCount() + 1);
                        } else {
                            stat.getRetried().add(updateResult.getAccount());
                        }
                    }
                    return stat;
                });
    }

    private CompletableFuture<AccountUpdateResult> updateAccountAsync(TrackTargetDTO account) {
        return alternativaService.getTracking(account.getName())
                .thenApply(alternativaMapper::toFullAccountData)
                .thenAccept(data -> trackStoreService.updateTargetData(account.getId(), data.getTrackData(), data.isHasPremium()))
                .handle((ignored, ex) -> {
                    if (ex == null) {
                        log.info("[{}] Updated {}", account.getId(), account.getName());
                        updateAccountStatusIfNeed(account, ACTIVE);
                        return AccountUpdateResult.processed(account);
                    }

                    Throwable cause = ex.getCause();
                    if (cause instanceof AlternativaTooManyRequestsException) {
                        log.info("[{}] Too many requests due updating {}", account.getId(),
                                account.getName());
                        return AccountUpdateResult.retrying(account);
                    }

                    if (cause instanceof AlternativaException ||
                            cause instanceof InvalidTrackDataException) {
                        log.warn("[{}] Exception due updating {}: {}", account.getId(),
                                account.getName(), cause.getMessage());
                    } else {
                        log.error("[{}] Exception due updating {}", account.getId(),
                                account.getName(), cause);
                    }

                    TrackTargetStatus status = cause instanceof InvalidTrackDataException ||
                            cause instanceof AlternativaUserNotFoundException ? DISABLED : FROZEN;
                    updateAccountStatusIfNeed(account, status);
                    return AccountUpdateResult.processed(account);
                });
    }

    private void updateAccountStatusIfNeed(TrackTargetDTO account, TrackTargetStatus newStatus) {
        if (account.getStatus() == PREMIUM) {
            return;
        }
        if (account.getStatus() != newStatus) {
            TrackTargetStatus oldStatus = account.getStatus();
            account.setStatus(newStatus);
            targetService.update(account.getId(), account);
            if (oldStatus == SLEEP && newStatus == ACTIVE) {
                return;
            }
            log.warn("[{}] Changed account {} status from {} to {}", account.getId(),
                    account.getName(), oldStatus, account.getStatus());
        }
    }

    private List<TrackTargetDTO> getAccounts(Set<TrackTargetStatus> statuses) {
        TrackTargetFilter filter = new TrackTargetFilter();
        filter.setTargetType(TrackTargetType.ACCOUNT);

        filter.setStatuses(statuses);
        Sort sort = Sort.by("id");

        return targetService.findAll(filter, sort);
    }

    @SneakyThrows
    private void sleep(Duration duration) {
        Thread.sleep(duration.toMillis());
    }

}