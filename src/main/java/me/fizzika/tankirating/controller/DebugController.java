package me.fizzika.tankirating.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.dto.target.TrackTargetDTO;
import me.fizzika.tankirating.enums.SnapshotPeriod;
import me.fizzika.tankirating.enums.track.TrackTargetType;
import me.fizzika.tankirating.repository.tracking.TrackDiffRepository;
import me.fizzika.tankirating.repository.tracking.TrackSnapshotRepository;
import me.fizzika.tankirating.service.online.OnlineUpdateService;
import me.fizzika.tankirating.service.tracking.internal.TrackingUpdateService;
import me.fizzika.tankirating.service.tracking.sanitizer.impl.DisabledAccountSanitizer;
import me.fizzika.tankirating.service.tracking.sanitizer.impl.FrozenAccountsSanitizer;
import me.fizzika.tankirating.service.tracking.sanitizer.impl.HeadSnapshotSanitizer;
import me.fizzika.tankirating.service.tracking.sanitizer.impl.SleepAccountsSanitizer;
import me.fizzika.tankirating.service.tracking.sanitizer.impl.TtlDiffSanitizer;
import me.fizzika.tankirating.service.tracking.sanitizer.impl.TtlSnapshotSanitizer;
import me.fizzika.tankirating.service.tracking.target.TrackTargetService;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("debug")
@RequiredArgsConstructor
@RequestMapping("/debug")
@Slf4j
@Tag(name = "Debug", description = "API for doing debug stuff")
public class DebugController {

    private final TrackingUpdateService updateService;
    private final OnlineUpdateService onlineUpdateService;
    private final TrackTargetService targetService;
    private final HeadSnapshotSanitizer headSnapshotSanitizer;
    private final FrozenAccountsSanitizer frozenAccountsSanitizer;
    private final SleepAccountsSanitizer sleepAccountsSanitizer;

    private final TtlSnapshotSanitizer ttlSnapshotSanitizer;
    private final TtlDiffSanitizer ttlDiffSanitizer;
    private final DisabledAccountSanitizer disabledAccountSanitizer;

    private final TrackDiffRepository diffRepository;
    private final TrackSnapshotRepository snapshotRepository;

    @PostMapping("/update/{account}")
    public void updateOne(@PathVariable String account) {
        TrackTargetDTO target = targetService.getByName(account, TrackTargetType.ACCOUNT);
        updateService.updateOne(target);
    }

    @PostMapping("/update/online")
    public void updateOnline() {
        onlineUpdateService.updateOnline();
    }

    @PostMapping("/sanitizer/head")
    public void headSanitizer() {
        headSnapshotSanitizer.sanitize();
    }

    @PostMapping("/sanitizer/frozen")
    public void frozenSanitizer() {
        frozenAccountsSanitizer.sanitize();
    }

    @PostMapping("/sanitizer/sleep")
    public void sleepSanitizer() {
        sleepAccountsSanitizer.sanitize();
    }

    @PostMapping("/sanitizer/disabled")
    public void disabledSanitizer() {
        disabledAccountSanitizer.sanitize();
    }

    @PostMapping("/sanitizer/ttl/snapshot")
    public void snapshotSanitizer() {
        ttlSnapshotSanitizer.sanitize();
    }

    @PostMapping("/sanitizer/ttl/diff")
    public void fullDiffSanitizer() {
        ttlDiffSanitizer.sanitize();
    }

    @GetMapping("/periods/{targetId}")
    public Set<SnapshotPeriod> getPeriods(@PathVariable Integer targetId) {
        return snapshotRepository.findCurrentPeriods(targetId, LocalDateTime.now());
    }
}