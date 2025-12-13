package me.fizzika.tankirating.service.tracking.sanitizer.impl;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.config.properties.TtlProperties;
import me.fizzika.tankirating.enums.DiffPeriodUnit;
import me.fizzika.tankirating.repository.tracking.TrackActivityRepository;
import me.fizzika.tankirating.repository.tracking.TrackUsageRepository;
import me.fizzika.tankirating.service.tracking.sanitizer.TrackSanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TtlDiffSanitizer implements TrackSanitizer {

    @Autowired
    private TtlProperties ttlProperties;
    @Autowired
    private TrackActivityRepository trackActivityRepository;
    @Autowired
    private TrackUsageRepository trackUsageRepository;

    @Override
    @Transactional
    public void sanitize() {
        Map<DiffPeriodUnit, Period> fullDiffTtls = ttlProperties.getFullDiff();

        int totalActivitiesDeleted = 0;
        int totalUsagesDeleted =0;
        for (DiffPeriodUnit periodUnit : fullDiffTtls.keySet()) {
            Period ttl = fullDiffTtls.get(periodUnit);
            log.info("Period {}, Ttl = {}", periodUnit, ttl);
            LocalDateTime deleteBefore = LocalDateTime.now().minus(ttl);

            int activityDeleted = trackActivityRepository.deleteDiffActivities(periodUnit, deleteBefore);
            int usagesDeleted = trackUsageRepository.deleteDiffUsages(periodUnit, deleteBefore);

            log.info("Period {}: Deleted {} activities, {} usages", periodUnit, activityDeleted, usagesDeleted);

            totalActivitiesDeleted += activityDeleted;
            totalUsagesDeleted += usagesDeleted;
        }
        log.info("Total deleted: {} activities, {} usages", totalActivitiesDeleted, totalUsagesDeleted);
    }
}