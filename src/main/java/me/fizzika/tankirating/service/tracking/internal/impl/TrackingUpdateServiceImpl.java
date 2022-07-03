package me.fizzika.tankirating.service.tracking.internal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.dto.TrackTargetDTO;
import me.fizzika.tankirating.enums.track.GroupMeta;
import me.fizzika.tankirating.enums.track.TrackTargetType;
import me.fizzika.tankirating.mapper.AlternativaTrackingMapper;
import me.fizzika.tankirating.model.UserData;
import me.fizzika.tankirating.model.track_data.TrackFullData;
import me.fizzika.tankirating.service.tracking.*;
import me.fizzika.tankirating.service.tracking.internal.AlternativaTrackingService;
import me.fizzika.tankirating.service.tracking.internal.TrackStoreService;
import me.fizzika.tankirating.service.tracking.internal.TrackingUpdateService;
import org.springframework.stereotype.Service;

import java.util.*;
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
                            if (ex == null) {
                                log.info("All account has been successfully updated");
                            } else {
                                log.warn("There are some problem during account updating, check logs");
                            }
                        }).join();

        log.info("Groups update has been started");
        List<TrackTargetDTO> groups = targetService.getAll(TrackTargetType.GROUP);
        log.info("Found {} groups: {}", groups.size(), groups);

        for (TrackTargetDTO group : groups) {
            Optional<GroupMeta> meta = GroupMeta.fromName(group.getName());
            if (meta.isEmpty()) {
                log.warn("Don't found meta for {} group", group.getName());
                continue;
            }
            trackStoreService.updateGroupData(group.getId(), meta.get());
            log.info("Group {} has been updated", meta.get().name());
        }
    }

    private CompletableFuture<Void> updateAccountAsync(Integer targetId, String nickname) {
        return updateAccountAsync(fetchData(nickname), nickname, targetId);
    }

    private CompletableFuture<UserData<TrackFullData>> fetchData(String nickname) {
        return alternativaService.getTracking(nickname)
                .thenApply(alternativaMapper::toFullUserData);
    }

    private CompletableFuture<Void> updateAccountAsync(CompletableFuture<UserData<TrackFullData>> userDataFetcher,
                                                       String nickname,
                                                       Integer targetId) {
        return userDataFetcher
                .thenAccept(data -> trackStoreService.updateTargetData(targetId, data.getTrackData(), data.isHasPremium()))
                .whenComplete((res, ex) -> {
                    if (ex == null) {
                        log.info("Updated {}", nickname);
                    } else {
                        log.error("Error due updating {}", nickname, ex);
                    }
                });
    }

    private static Map<GroupMeta, TrackFullData> getTrackGroupDataMap() {
        return Arrays.stream(GroupMeta.values())
                .collect(Collectors.toMap(Function.identity(), (ignored) -> new TrackFullData(), (fst, snd) -> fst,
                        () -> new EnumMap<>(GroupMeta.class)));
    }


}
