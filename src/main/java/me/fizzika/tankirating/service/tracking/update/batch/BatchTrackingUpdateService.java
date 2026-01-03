package me.fizzika.tankirating.service.tracking.update.batch;

import static me.fizzika.tankirating.enums.track.TrackTargetStatus.FROZEN;
import static me.fizzika.tankirating.enums.track.TrackTargetStatus.PREMIUM;

import io.github.bucket4j.Bucket;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.dto.filter.TrackTargetFilter;
import me.fizzika.tankirating.dto.target.TrackTargetDTO;
import me.fizzika.tankirating.enums.track.TrackTargetStatus;
import me.fizzika.tankirating.enums.track.TrackTargetType;
import me.fizzika.tankirating.model.AccountUpdateResult;
import me.fizzika.tankirating.model.AccountsUpdateStat;
import me.fizzika.tankirating.model.TrackGroup;
import me.fizzika.tankirating.service.tracking.internal.AlternativaTrackingService;
import me.fizzika.tankirating.service.tracking.internal.TrackStoreService;
import me.fizzika.tankirating.service.tracking.target.TrackTargetService;
import me.fizzika.tankirating.service.tracking.update.TrackingUpdateService;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchTrackingUpdateService {

    private final TrackingUpdateService trackingUpdateService;
    private final BatchUpdateBucketFactory batchUpdateBucketFactory;

    private final AlternativaTrackingService alternativaService;
    private final TrackTargetService targetService;
    private final TrackStoreService trackStoreService;
    private final ScheduledExecutorService scheduledExecutorService;

    private final Map<TrackTargetStatus, Lock> lockMap = new EnumMap<>(TrackTargetStatus.class);

    public void updateGroups() {
        LocalDateTime startAt = LocalDateTime.now();

        log.info("Groups update has been started");
        List<TrackGroup> groups = targetService.getAllGroups();
        log.info("Found {} groups: {}", groups.size(), groups);
        groups.forEach(trackStoreService::updateCurrentGroupData);

        Duration updateDuration = Duration.between(startAt, LocalDateTime.now());
        log.info("Groups update finished. Duration: {}", updateDuration);
    }

    public void updateAccounts(TrackTargetStatus targetStatus) {
        lockMap.putIfAbsent(targetStatus, new ReentrantLock());
        Lock lock = lockMap.get(targetStatus);
        if (!lock.tryLock()) {
            log.error("Update {} is running, skipping and exiting", targetStatus);
            return;
        }
        try {
            List<TrackTargetDTO> accounts = getAccounts(Set.of(targetStatus));
            Bucket batchUpdateBucket = batchUpdateBucketFactory.createBucketForBatchUpdate(targetStatus, accounts.size()).orElse(null);
            LocalDateTime startAt = LocalDateTime.now();
            log.info("Start updating {} {} accounts", accounts.size(), targetStatus);
            doUpdate(accounts, batchUpdateBucket);
            Duration updateDuration = Duration.between(startAt, LocalDateTime.now());
            log.info("Finish updating {} {} accounts. Duration: {}", accounts.size(), targetStatus, updateDuration);
        } finally {
            lock.unlock();
        }
    }

    private List<TrackTargetDTO> getAccounts(Set<TrackTargetStatus> statuses) {
        TrackTargetFilter filter = new TrackTargetFilter();
        filter.setTargetType(TrackTargetType.ACCOUNT);

        filter.setStatuses(statuses);
        Sort sort = Sort.by("id");

        return targetService.findAll(filter, sort);
    }

    private void doUpdate(List<TrackTargetDTO> accounts, Bucket batchUpdateBucket) {
        try {
            alternativaService.healthCheck().join();
        } catch (CompletionException ex) {
            log.error("Alternativa service is unavailable ", ex.getCause());
            log.error("Skipping accounts updating");
            return;
        }

        log.info("Found {} accounts", accounts.size());
        updateAccounts(accounts, batchUpdateBucket);
    }

    private void updateAccounts(Collection<TrackTargetDTO> accounts, Bucket batchUpdateBucket) {
        AccountsUpdateStat stat = updateAccountsAsync(accounts, batchUpdateBucket).join();

        log.info(stat.toReportString());
        stat.getRetried().forEach(this::markAccountAsFrozen);
    }

    private CompletableFuture<AccountsUpdateStat> updateAccountsAsync(Collection<TrackTargetDTO> accounts, Bucket batchUpdateBucket) {
        List<CompletableFuture<AccountUpdateResult>> updaters = accounts.stream()
                                                                        .map(acc -> updateAccountAsync(batchUpdateBucket, acc))
                                                                        .toList();

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
                                        if (updateResult.getExceptionClass() != null) {
                                            stat.getExceptionStats().merge(updateResult.getExceptionClass(),
                                                                           1, Integer::sum);
                                        }
                                    }
                                    return stat;
                                });
    }

    private CompletableFuture<AccountUpdateResult> updateAccountAsync(@Nullable Bucket bucket, TrackTargetDTO account) {
        if (bucket == null) {
            return trackingUpdateService.updateAccountAsync(account, false);
        }

        return bucket.asScheduler()
                     .consume(1, scheduledExecutorService)
                     .thenComposeAsync(handle -> trackingUpdateService.updateAccountAsync(account, false));
    }

    private void markAccountAsFrozen(TrackTargetDTO account) {
        if (account.getStatus() == FROZEN || account.getStatus() == PREMIUM) {
            return;
        }
        account.setStatus(FROZEN);
        targetService.update(account.getId(), account);
        log.info("[{}/{}] Marked as Frozen", account.getId(),
                 account.getName());
    }
}