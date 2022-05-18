package me.fizzika.tankirating.controller;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.TrackTargetDTO;
import me.fizzika.tankirating.dto.tracking.TrackSnapshotDTO;
import me.fizzika.tankirating.enums.TrackFormat;
import me.fizzika.tankirating.enums.track.TrackTargetType;
import me.fizzika.tankirating.service.tracking.TrackTargetSnapshotService;
import me.fizzika.tankirating.service.tracking.TrackTargetService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account/{nickname}/snapshot")
public class TrackSnapshotController {
    
    private final TrackTargetSnapshotService trackTargetSnapshotService;
    private final TrackTargetService trackTargetService;

    @GetMapping("/latest")
    public TrackSnapshotDTO getLatestSnapshot(@PathVariable String nickname, @RequestParam TrackFormat format) {
        return trackTargetSnapshotService.getLatestSnapshot(getTarget(nickname), format);
    }

    @GetMapping("/init")
    public TrackSnapshotDTO getInitSnapshot(@PathVariable String nickname, @RequestParam TrackFormat format) {
        return trackTargetSnapshotService.getInitSnapshot(getTarget(nickname), format);
    }

    public TrackTargetDTO getTarget(String name) {
        return trackTargetService.getByName(name, TrackTargetType.ACCOUNT);
    }

}
