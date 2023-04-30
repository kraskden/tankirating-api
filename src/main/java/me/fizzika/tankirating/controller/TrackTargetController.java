package me.fizzika.tankirating.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.TrackTargetDTO;
import me.fizzika.tankirating.dto.filter.TrackTargetFilter;
import me.fizzika.tankirating.enums.track.TrackTargetType;
import me.fizzika.tankirating.model.validation.Create;
import me.fizzika.tankirating.service.tracking.TrackTargetService;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    public Page<TrackTargetDTO> findAll(@ParameterObject  @Valid TrackTargetFilter filter,
                                        @ParameterObject Pageable pageable) {
        return trackTargetService.findAll(filter, pageable);
    }

}
