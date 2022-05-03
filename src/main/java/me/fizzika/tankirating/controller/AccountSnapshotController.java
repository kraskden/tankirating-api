package me.fizzika.tankirating.controller;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.tracking.TrackSnapshotDTO;
import me.fizzika.tankirating.enums.TrackFormat;
import me.fizzika.tankirating.service.AccountSnapshotService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account/{nickname}/snapshot")
public class AccountSnapshotController {
    
    private final AccountSnapshotService accountSnapshotService;

    @GetMapping("/latest")
    public TrackSnapshotDTO getLatestSnapshot(@PathVariable String nickname, @RequestParam TrackFormat format) {
        return accountSnapshotService.getLatestSnapshot(nickname, format);
    }

    @GetMapping("/init")
    public TrackSnapshotDTO getInitSnapshot(@PathVariable String nickname, @RequestParam TrackFormat format) {
        return accountSnapshotService.getInitSnapshot(nickname, format);
    }

}
