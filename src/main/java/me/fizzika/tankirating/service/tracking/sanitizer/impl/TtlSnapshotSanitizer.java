package me.fizzika.tankirating.service.tracking.sanitizer.impl;

//import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.config.properties.TtlProperties;
import me.fizzika.tankirating.enums.SnapshotPeriod;
import me.fizzika.tankirating.enums.track.TrackTargetStatus;
import me.fizzika.tankirating.repository.tracking.TrackRepository;
import me.fizzika.tankirating.repository.tracking.TrackSnapshotRepository;
import me.fizzika.tankirating.service.tracking.sanitizer.TrackSanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TtlSnapshotSanitizer implements TrackSanitizer {

    @Autowired
    private TtlProperties ttlProperties;
    @Autowired
    private TrackRepository trackRepository;

    @Override
    @Transactional
    public void sanitize() {
        log.info("Start {} sanitizer", this.getClass().getSimpleName());

        List<SnapshotPeriod> snapshotPeriods = ttlProperties.getSnapshot().keySet()
                .stream()
                .sorted(Comparator.reverseOrder())
                .toList();

        for (SnapshotPeriod period : snapshotPeriods) {
            Period ttl = ttlProperties.getSnapshot().get(period);

            log.info("Period = {}, Ttl = {}", period, ttl);
            LocalDateTime deleteBefore = LocalDateTime.now().minus(ttl);
            int deletedCount = trackRepository.deleteSnapshotTracks(period, deleteBefore);
            log.info("Deleted {} tracks for period {}", deletedCount, period);
        }
    }
}