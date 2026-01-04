package me.fizzika.tankirating.service.tracking.maintenance.jobs.status;

import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.repository.tracking.TrackTargetRepository;
import me.fizzika.tankirating.service.tracking.maintenance.MaintenanceJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Marks frozen accounts as blocked if they aren't updated more than N days
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FrozenToDisabledMaintenanceJob extends MaintenanceJob {

    @Value("${app.tracking.frozen-to-disabled-timeout}")
    private Duration frozenToDisabledDuration;

    private final TrackTargetRepository repository;

    @Override
    public void runMaintenance() {
        LocalDateTime minUpdateDate = LocalDateTime.now().minus(frozenToDisabledDuration);
        repository.markFrozenAccountsAsBlocked(minUpdateDate);
    }
}