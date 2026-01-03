package me.fizzika.tankirating.service.tracking.update;

import static me.fizzika.tankirating.enums.track.TrackTargetStatus.ACTIVE;
import static me.fizzika.tankirating.enums.track.TrackTargetStatus.DISABLED;
import static me.fizzika.tankirating.enums.track.TrackTargetStatus.FROZEN;
import static me.fizzika.tankirating.enums.track.TrackTargetStatus.PREMIUM;

import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.dto.target.TrackTargetDTO;
import me.fizzika.tankirating.enums.track.TrackTargetStatus;
import me.fizzika.tankirating.exceptions.alternativa.AlternativaTooManyRequestsException;
import me.fizzika.tankirating.exceptions.alternativa.AlternativaUserNotFoundException;
import me.fizzika.tankirating.exceptions.tracking.InvalidTrackDataException;
import me.fizzika.tankirating.mapper.AlternativaTrackingMapper;
import me.fizzika.tankirating.model.AccountUpdateResult;
import me.fizzika.tankirating.service.tracking.internal.AlternativaTrackingService;
import me.fizzika.tankirating.service.tracking.internal.TrackStoreService;
import me.fizzika.tankirating.service.tracking.target.TrackTargetService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackingUpdateService {

    private final AlternativaTrackingService alternativaService;
    private final AlternativaTrackingMapper alternativaMapper;
    private final TrackTargetService targetService;
    private final TrackStoreService trackStoreService;

    public AccountUpdateResult updateAccount(TrackTargetDTO account) {
        AccountUpdateResult res = updateAccountAsync(account, true).join();
        if (res.getThrowable() != null) {
            log.error("Failed to update {}", account.getName(), res.getThrowable());
        }
        return res;
    }

    public CompletableFuture<AccountUpdateResult> updateAccountAsync(TrackTargetDTO account, boolean saveException) {
        return alternativaService.getTracking(account.getName())
                                 .thenApply(alternativaMapper::toFullAccountData)
                                 .thenAccept(data -> trackStoreService.updateTargetData(account.getId(), data.getTrackData(), data.isHasPremium()))
                                 .handle((ignored, ex) -> {
                                     if (ex == null) {
                                         log.debug("[{}] Updated {}", account.getId(), account.getName());
                                         updateAccountStatusIfNeed(account, ACTIVE);
                                         return AccountUpdateResult.ok(account);
                                     }

                                     Throwable cause = ex.getCause();
                                     if (cause instanceof AlternativaTooManyRequestsException) {
                                         log.debug("[{}] Too many requests due updating {}", account.getId(),
                                                   account.getName());
                                         return AccountUpdateResult.retrying(account);
                                     }

                                     log.debug("[{}] Exception due updating {}: {}", account.getId(),
                                               account.getName(), cause.getMessage());

                                     TrackTargetStatus status = cause instanceof InvalidTrackDataException ||
                                             cause instanceof AlternativaUserNotFoundException ? DISABLED : FROZEN;
                                     updateAccountStatusIfNeed(account, status);
                                     if (saveException) {
                                         return AccountUpdateResult.failed(account, ex);
                                     } else {
                                         return AccountUpdateResult.failed(account, ex.getClass().getSimpleName());
                                     }
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
            if (newStatus != DISABLED && newStatus != FROZEN) {
                return;
            }
            log.info("[{}] Changed account {} status from {} to {}", account.getId(),
                     account.getName(), oldStatus, account.getStatus());
        }
    }
}