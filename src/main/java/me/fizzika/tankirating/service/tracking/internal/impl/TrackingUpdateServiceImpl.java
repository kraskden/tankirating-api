package me.fizzika.tankirating.service.tracking.internal.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.dto.TrackTargetDTO;
import me.fizzika.tankirating.dto.filter.TrackTargetFilter;
import me.fizzika.tankirating.enums.track.TrackTargetStatus;
import me.fizzika.tankirating.enums.track.TrackTargetType;
import me.fizzika.tankirating.mapper.AlternativaTrackingMapper;
import me.fizzika.tankirating.model.TrackGroup;
import me.fizzika.tankirating.service.tracking.TrackTargetService;
import me.fizzika.tankirating.service.tracking.internal.AlternativaTrackingService;
import me.fizzika.tankirating.service.tracking.internal.TrackStoreService;
import me.fizzika.tankirating.service.tracking.internal.TrackingUpdateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static me.fizzika.tankirating.enums.track.TrackTargetStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackingUpdateServiceImpl implements TrackingUpdateService {

    private static final int ACCOUNT_UPDATE_BUFFER = 100;

    private final AlternativaTrackingService alternativaService;
    private final AlternativaTrackingMapper alternativaMapper;

    private final TrackTargetService targetService;
    private final TrackStoreService trackStoreService;

    @Override
    public void updateAll() {
        log.info("Starting accounts updating");
        log.info("Updating parameters: buffer={}, timeout=5s", ACCOUNT_UPDATE_BUFFER);
        Page<TrackTargetDTO> buff = getAccountsPage(1);

        long total = buff.getTotalElements();
        long processed = 0;

        log.info("Found {} accounts", total);

        while (!buff.getContent().isEmpty()) {
            updateAccounts(buff.getContent()).join();
            processed += buff.getNumberOfElements();

            log.info("[{}/{}] Processed", processed, total);

            if (buff.hasNext()) {
                sleep(Duration.ofSeconds(5));
            }
            buff = buff.hasNext() ? getAccountsPage(buff.getNumber() + 1)
                    : Page.empty();
        }
        log.info("All accounts has been processed");

        log.info("Groups update has been started");
        List<TrackGroup> groups = targetService.getAllGroups();
        log.info("Found {} groups: {}", groups.size(), groups);
        groups.forEach(trackStoreService::updateCurrentGroupData);
        log.info("Groups update finished");
    }

    private CompletableFuture<Void> updateAccounts(Collection<TrackTargetDTO> accounts) {
        CompletableFuture<?>[] updaters = accounts.stream()
                .map(this::updateAccountAsync)
                .toArray(CompletableFuture<?>[]::new);
        return CompletableFuture.allOf(updaters);
    }

    private CompletableFuture<Void> updateAccountAsync(TrackTargetDTO account) {
        return alternativaService.getTracking(account.getName())
                .thenApply(alternativaMapper::toFullAccountData)
                .thenAccept(data -> trackStoreService.updateTargetData(account.getId(), data.getTrackData(), data.isHasPremium()))
                .whenComplete((ignored, ex) -> {
                    if (ex != null) {
                        log.error("Error in updating {}", account.getName(), ex);
                    } else {
                        log.info("Updated {}", account.getName());
                    }
                    TrackTargetStatus newStatus = ex != null ? ACTIVE : FROZEN;
                    if (account.getStatus() != newStatus) {
                        account.setStatus(newStatus);
                        targetService.update(account.getId(), account);
                    }
                });
    }

    private Page<TrackTargetDTO> getAccountsPage(int page) {
        TrackTargetFilter filter = new TrackTargetFilter();
        filter.setTargetType(TrackTargetType.ACCOUNT);
        filter.setStatuses(List.of(ACTIVE, FROZEN));
        Sort sort = Sort.by("id");

        return targetService.findAll(filter, PageRequest.of(page, ACCOUNT_UPDATE_BUFFER, sort));
    }

    @SneakyThrows
    private void sleep(Duration duration) {
        Thread.sleep(duration);
    }

}
