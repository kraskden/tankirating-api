package me.fizzika.tankirating.service.tracking.maintenance.jobs;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.repository.tracking.TrackRepository;
import me.fizzika.tankirating.repository.tracking.TrackTargetRepository;
import me.fizzika.tankirating.service.tracking.maintenance.MaintenanceJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteDisabledAccountMaintenanceJob extends MaintenanceJob {

    private final TrackTargetRepository targetRepository;
    private final TrackRepository trackRepository;

    @Value("${app.tracking.disabled-delete-timeout}")
    private Period disabledDeleteTimeout;

    @Override
    public void runMaintenance() {
        LocalDateTime disabledBeforeAt = LocalDateTime.now().minus(disabledDeleteTimeout);

        Set<Integer> disabledIds = targetRepository.findDisabledUpdatedBeforeAt(disabledBeforeAt);
        log.info("Disabled accounts to delete: {}", disabledIds.size());
        trackRepository.deleteAccountTracks(disabledIds);
        targetRepository.deleteAllByIdInBatch(disabledIds);
    }
}