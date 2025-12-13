package me.fizzika.tankirating.service.tracking.maintenance.jobs;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.repository.tracking.TrackTargetRepository;
import me.fizzika.tankirating.service.tracking.maintenance.MaintenanceJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MarkActiveAccountsAsSleepMaintenanceJob extends MaintenanceJob {

    @Value("${app.tracking.active-to-sleep-timeout}")
    private Period activeToSleepTimeout;

    @Resource
    private TrackTargetRepository targetRepository;

    @Override
    public void runMaintenance() {
        LocalDateTime minActivityDate = LocalDate.now()
                .minus(activeToSleepTimeout)
                .atStartOfDay();

        targetRepository.markActiveAccountsAsSleep(minActivityDate);
    }
}