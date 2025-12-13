package me.fizzika.tankirating.service.tracking.maintenance;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.exceptions.ExternalException;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MaintenanceService {

    private final List<MaintenanceJob> maintenanceJobs;

    @Scheduled(cron = "${app.cron.maintenance}")
    public void runMaintenance() {
        LocalDateTime startAt = LocalDateTime.now();
        log.info("Run maintenance");
        for (MaintenanceJob job : maintenanceJobs) {
            try {
                job.start();
            } catch (Exception e) {
                log.error("Failed to execute maintenance: {}, cause:", job.getName(), e);
            }
        }
        log.info("Finish maintenance in {}", Duration.between(startAt, LocalDateTime.now()));
    }

    public List<String> getJobNames() {
        return maintenanceJobs.stream()
                              .map(MaintenanceJob::getName)
                              .toList();
    }

    public void runJob(String name) {
        var job = maintenanceJobs.stream()
                                 .filter(j -> j.getName().equalsIgnoreCase(name))
                                 .findFirst()
                                 .orElseThrow(() -> new ExternalException("Job not found", HttpStatus.NOT_FOUND));
        job.start();
    }
}