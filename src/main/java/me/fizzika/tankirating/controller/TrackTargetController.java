package me.fizzika.tankirating.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.target.TrackTargetDTO;
import me.fizzika.tankirating.dto.filter.TrackTargetFilter;
import me.fizzika.tankirating.enums.track.TrackTargetType;
import me.fizzika.tankirating.service.tracking.target.TrackTargetService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/target")
@Tag(name = "Track target", description = "API for manipulating track targets (accounts, groups)")
public class TrackTargetController {

    private final TrackTargetService trackTargetService;

    @GetMapping("/{name}")
    public TrackTargetDTO getTarget(@PathVariable String name, @RequestParam(defaultValue = "ACCOUNT")
            TrackTargetType targetType) {
        return trackTargetService.getByName(name, targetType);
    }

    @GetMapping
    public PagedModel<TrackTargetDTO> findAll(@org.springdoc.core.annotations.ParameterObject @Valid TrackTargetFilter filter,
                                              @ParameterObject Pageable pageable) {
        Page<TrackTargetDTO> all = trackTargetService.findAll(filter, pageable);
        return new PagedModel<>(all);
    }
}