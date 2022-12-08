package me.fizzika.tankirating.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.tracking.TrackSnapshotDTO;
import me.fizzika.tankirating.enums.track.TrackFormat;
import me.fizzika.tankirating.service.tracking.TrackTargetSnapshotService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/target/{targetId}/snapshot")
@Tag(name = "Snapshot", description = "Provide account state for a specific date")
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

    @GetMapping("/{date}")
    public TrackSnapshotDTO getForDate(@PathVariable Integer targetId,
                                       @Parameter(example = "2022-06-13") @PathVariable LocalDate date,
                                       @RequestParam TrackFormat format) {
        return trackTargetSnapshotService.getSnapshotForDate(targetId, date, format);
    }

}
