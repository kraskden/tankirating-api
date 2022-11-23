package me.fizzika.tankirating.service.tracking.sanitizer.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.repository.tracking.TrackTargetRepository;
import me.fizzika.tankirating.service.tracking.sanitizer.TrackSanitizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Marks frozen accounts as blocked if they aren't updated more than N days
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FrozenAccountsSanitizer implements TrackSanitizer {

    @Value("${app.tracking.frozen-to-disabled-timeout}")
    private Duration frozenToDisabledDuration;

    private final TrackTargetRepository repository;

    @Override
    @Scheduled(cron = "${app.cron.frozen-account-sanitizer}")
    public void sanitize() {
        log.info("Start {} sanitizer (period={}d)", this.getClass().getSimpleName(),
                frozenToDisabledDuration.toDays());
        LocalDateTime minUpdateDate = LocalDateTime.now().minus(frozenToDisabledDuration);

        int updated = repository.markFrozenAccountsAsBlocked(minUpdateDate);
        log.info("Mark {} frozen accounts as disabled", updated);
        log.info("Finish {} sanitizer", this.getClass().getSimpleName());
    }

}
