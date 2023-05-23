package me.fizzika.tankirating.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.fizzika.tankirating.dto.management.TrackRebuildParams;
import me.fizzika.tankirating.service.management.DailyDiffRebuilder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@Tag(name = "Management", description = "Admin panel for TankiRating.org")
@RequestMapping("/management")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "auth")
public class ManagementController {

    @Resource
    private DailyDiffRebuilder dailyDiffRebuilder;

    @PostMapping("/rebuildDailyDiff")
    public void rebuildDailyDiff(@RequestBody @Valid TrackRebuildParams rebuildParams) {
        dailyDiffRebuilder.rebuildDailyDiffs(rebuildParams);
    }
}
