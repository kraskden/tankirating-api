package me.fizzika.tankirating.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.service.tracking.maintenance.MaintenanceService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Maintenance", description = "Run maintenance jobs")
@RequestMapping("/maintenance")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "auth")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @GetMapping("/jobs")
    public List<String> getJobs() {
        return maintenanceService.getJobNames();
    }

    @PostMapping("/jobs/{jobName}")
    public void runJob(@PathVariable String jobName) {
        maintenanceService.runJob(jobName);
    }
}