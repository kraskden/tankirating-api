package me.fizzika.tankirating.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.fizzika.tankirating.dto.management.TargetStatEntryDTO;
import me.fizzika.tankirating.dto.management.TrackRebuildParams;
import me.fizzika.tankirating.service.management.DailyDiffRebuilder;
import me.fizzika.tankirating.service.management.StatService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@Tag(name = "Management", description = "Admin panel for TankiRating.org")
@RequestMapping("/management")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "auth")
public class ManagementController {

    @Resource
    private DailyDiffRebuilder dailyDiffRebuilder;
    @Resource
    private StatService statService;

    @PostMapping("/rebuildDailyDiff")
    public void rebuildDailyDiff(@RequestBody @Valid TrackRebuildParams rebuildParams) {
        dailyDiffRebuilder.rebuildDailyDiffs(rebuildParams);
    }

    @GetMapping("/stat/targets")
    public List<TargetStatEntryDTO> targetStat() {
        return statService.getTargetStat();
    }
}
