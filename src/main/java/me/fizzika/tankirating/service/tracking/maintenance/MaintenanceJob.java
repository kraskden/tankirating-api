package me.fizzika.tankirating.service.tracking.maintenance;

import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class MaintenanceJob {

    @Transactional
    public void start() {
        log.info("Start maintenance {}", getName());
        LocalDateTime startAt = LocalDateTime.now();
        runMaintenance();
        Duration executionTime = Duration.between(startAt, LocalDateTime.now());
        log.info("Finish maintenance {}, executionTime = {}", getName(), executionTime);
    }

    protected abstract void runMaintenance();

    protected String getName() {
        return getClass().getSimpleName();
    }
}