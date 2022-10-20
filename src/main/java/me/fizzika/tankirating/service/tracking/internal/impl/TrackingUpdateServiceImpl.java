package me.fizzika.tankirating.service.tracking.internal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.dto.TrackTargetDTO;
import me.fizzika.tankirating.enums.track.GroupMeta;
import me.fizzika.tankirating.enums.track.TrackTargetType;
import me.fizzika.tankirating.mapper.AlternativaTrackingMapper;
import me.fizzika.tankirating.model.AccountData;
import me.fizzika.tankirating.model.TrackGroup;
import me.fizzika.tankirating.model.track_data.TrackFullData;
import me.fizzika.tankirating.service.tracking.TrackTargetService;
import me.fizzika.tankirating.service.tracking.internal.AlternativaTrackingService;
import me.fizzika.tankirating.service.tracking.internal.TrackStoreService;
import me.fizzika.tankirating.service.tracking.internal.TrackingUpdateService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackingUpdateServiceImpl implements TrackingUpdateService {

    private final AlternativaTrackingService alternativaService;
    private final AlternativaTrackingMapper alternativaMapper;
    
    private final TrackTargetService targetService;
    private final TrackStoreService trackStoreService;

    @Override
    public void updateAccount(Integer targetId, String nickname) {
        updateAccountAsync(targetId, nickname);
    }

    /**
     * Naive realisation
     *
     * The production-ready solution contains following steps:
     * 1) Split accounts into small groups (e.g. 30 account per group)
     * 2) Update accounts in the group
     * 3) Sleeping for a short period (e.g. minute)
     * 4) Fetch next group, go to the 2nd step
     * 5) When all groups are updated, update global stats
     */
    @Override
    public void updateAll() {
        log.info("Accounts update has been started");
        List<TrackTargetDTO> accounts = targetService.getAll(TrackTargetType.ACCOUNT).stream()
                .filter(t -> t.getStatus().isUpdatable())
                .collect(Collectors.toList());
        log.info("Found {} accounts", accounts.size());

        CompletableFuture<?>[] updaters = accounts.stream()
                .map(acc -> updateAccountAsync(acc.getId(), acc.getName()))
                .toArray(CompletableFuture<?>[]::new);

        CompletableFuture.allOf(updaters)
                        .whenComplete((ignored, ex) -> {
                            log.info("Accounts update finished");
                        }).join();

        log.info("Groups update has been started");
        List<TrackGroup> groups = targetService.getAllGroups();
        log.info("Found {} groups: {}", groups.size(), groups);
        groups.forEach(trackStoreService::updateCurrentGroupData);
        log.info("Groups update finished");
    }

    private CompletableFuture<Void> updateAccountAsync(Integer targetId, String nickname) {
        return alternativaService.getTracking(nickname)
                .thenApply(alternativaMapper::toFullAccountData)
                .thenAccept(data -> trackStoreService.updateTargetData(targetId, data.getTrackData(), data.isHasPremium()))
                .thenRun(() -> log.info("Updated {}", nickname))
                .whenComplete((ignored, ex) -> log.error("Error in updating {}", nickname, ex));
    }

}
