package me.fizzika.tankirating.v1_migration.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.enums.PeriodUnit;
import me.fizzika.tankirating.model.TrackGroup;
import me.fizzika.tankirating.model.date.PeriodDiffDates;
import me.fizzika.tankirating.repository.tracking.TrackDiffRepository;
import me.fizzika.tankirating.service.tracking.TrackTargetService;
import me.fizzika.tankirating.service.tracking.internal.TrackStoreService;
import me.fizzika.tankirating.v1_migration.service.V1MigrationService;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Order(1)
@RequiredArgsConstructor
@Profile("migration")
public class GroupMigrationService implements V1MigrationService {

    private final TrackTargetService trackTargetService;
    private final TrackDiffRepository diffRepository;
    private final TrackStoreService trackStoreService;

    @Override
    public void migrate() {
        LocalDateTime start = LocalDateTime.now();

        log.info("Starting group track creation...");
        List<TrackGroup> groups = trackTargetService.getAllGroups();
        log.info("Found {} groups: {}", groups.size(), groups);

        for (PeriodUnit period : PeriodUnit.GROUP_PERIODS) {
            List<PeriodDiffDates> diffDates = diffRepository.getAllPeriodDates(period);
            log.info("[{}] Starting groups creation...", period);
            log.info("[{}] Found {} existing entries", period, diffDates.size());
            for (TrackGroup group : groups) {
                log.info("[{}::{}] Start group build", period, group.getMeta().name());
                for (PeriodDiffDates diffDate : diffDates) {
                    trackStoreService.updateGroupDiff(group, period, diffDate);
                    log.debug("[{}::{}] Created group track for periodStart={}",
                            period, group.getMeta().name(), diffDate.getPeriodStart());
                }
                log.info("[{}::{}] Complete group build", period, group.getMeta().name());
            }
            log.info("[{}] All groups has been saved...", period);
        }
        log.info("All groups for all periods has been saved");

        Duration duration = Duration.between(start, LocalDateTime.now());
        log.info("Group track creation done in {} minutes and {} seconds", duration.toMinutes(),
                duration.toSecondsPart());
    }

}
