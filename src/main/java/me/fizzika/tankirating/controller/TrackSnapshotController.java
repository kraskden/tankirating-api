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
@RequestMapping("/target/{targetId}/snapshot")
public class TrackSnapshotController {
    
    private final TrackTargetSnapshotService trackTargetSnapshotService;

    @GetMapping("/latest")
    public TrackSnapshotDTO getLatestSnapshot(@PathVariable Integer targetId, @RequestParam TrackFormat format) {
        return trackTargetSnapshotService.getLatestSnapshot(targetId, format);
    }

    @GetMapping("/init")
    public TrackSnapshotDTO getInitSnapshot(@PathVariable Integer targetId, @RequestParam TrackFormat format) {
        return trackTargetSnapshotService.getInitSnapshot(targetId, format);
    }

}
