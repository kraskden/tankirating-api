package me.fizzika.tankirating.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import me.fizzika.tankirating.dto.management.TargetStatEntryDTO;
import me.fizzika.tankirating.dto.management.TrackRebuildParams;
import me.fizzika.tankirating.enums.track.TrackTargetStatus;
import me.fizzika.tankirating.service.management.DailyDiffRebuilder;
import me.fizzika.tankirating.service.management.StatService;
import me.fizzika.tankirating.service.tracking.RatingService;
import me.fizzika.tankirating.service.tracking.update.batch.BatchTrackingUpdateService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @Resource
    private BatchTrackingUpdateService batchTrackingUpdateService;
    @Resource
    private RatingService ratingService;

    @PostMapping("/rebuildDailyDiff")
    public void rebuildDailyDiff(@RequestBody @Valid TrackRebuildParams rebuildParams) {
        dailyDiffRebuilder.rebuildDailyDiffs(rebuildParams);
    }

    @GetMapping("/stat/targets")
    public List<TargetStatEntryDTO> targetStat() {
        return statService.getTargetStat();
    }

    @PostMapping("/update/{status}")
    public void doUpdate(@PathVariable TrackTargetStatus status) {
        batchTrackingUpdateService.updateAccounts(status);
    }

    @PostMapping("/update/groups")
    public void updateGroups() {
        batchTrackingUpdateService.updateGroups();
    }

    @PostMapping("/rating/resetCache")
    public void resetRatingsCache() {
        ratingService.resetCache();
    }
}