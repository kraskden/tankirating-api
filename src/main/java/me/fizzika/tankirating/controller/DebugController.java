package me.fizzika.tankirating.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.dto.TrackTargetDTO;
import me.fizzika.tankirating.enums.track.TrackTargetType;
import me.fizzika.tankirating.service.tracking.TrackTargetService;
import me.fizzika.tankirating.service.tracking.internal.TrackingUpdateService;
import me.fizzika.tankirating.service.tracking.sanitizer.impl.FrozenAccountsSanitizer;
import me.fizzika.tankirating.service.tracking.sanitizer.impl.HeadSnapshotSanitizer;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("debug")
@RequiredArgsConstructor
@RequestMapping("/debug")
@Slf4j
public class DebugController {

    private final TrackingUpdateService updateService;
    private final TrackTargetService targetService;
    private final HeadSnapshotSanitizer headSnapshotSanitizer;
    private final FrozenAccountsSanitizer frozenAccountsSanitizer;

    @PostMapping("/update/{account}")
    public void updateOne(@PathVariable String account) {
        TrackTargetDTO target = targetService.getByName(account, TrackTargetType.ACCOUNT);
        updateService.updateOne(target);
    }

    @PostMapping("/update/all")
    public void updateAll() {
        updateService.updateAll();
    }

    @PostMapping("/sanitizer/head")
    public void headSanitizer() {
        headSnapshotSanitizer.sanitize();
    }

    @PostMapping("/sanitizer/frozen")
    public void frozenSanitizer() {
        frozenAccountsSanitizer.sanitize();
    }

}
