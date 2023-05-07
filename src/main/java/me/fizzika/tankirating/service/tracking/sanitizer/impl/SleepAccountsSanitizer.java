package me.fizzika.tankirating.service.tracking.sanitizer.impl;

import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.enums.track.TrackTargetStatus;
import me.fizzika.tankirating.repository.tracking.TrackTargetRepository;
import me.fizzika.tankirating.service.tracking.sanitizer.TrackSanitizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Component
@Slf4j
public class SleepAccountsSanitizer implements TrackSanitizer {

    @Value("${app.tracking.active-to-sleep-timeout}")
    private Period activeToSleepTimeout;

    @Resource
    private TrackTargetRepository targetRepository;

    @Override
    @Transactional
    public void sanitize() {
        log.info("Start {} sanitizer (period={}d)", this.getClass().getSimpleName(),
                activeToSleepTimeout.getDays());

        LocalDateTime minActivityDate = LocalDate.now()
                .minus(activeToSleepTimeout)
                .atStartOfDay();

        targetRepository.markActiveAccountsAsSleep(minActivityDate);
        int sleepCount = targetRepository.countByStatus(TrackTargetStatus.SLEEP);
        log.info("There are {} sleep accounts after sanitizer run", sleepCount);
    }
}
