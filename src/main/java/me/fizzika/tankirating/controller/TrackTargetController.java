package me.fizzika.tankirating.controller;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.TrackTargetDTO;
import me.fizzika.tankirating.dto.filter.TrackTargetFilter;
import me.fizzika.tankirating.enums.track.TrackTargetType;
import me.fizzika.tankirating.model.validation.Create;
import me.fizzika.tankirating.service.tracking.TrackTargetService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/target")
public class TrackTargetController {

    private final TrackTargetService trackTargetService;

    @PostMapping
    public TrackTargetDTO addAccount(@Validated(Create.class) @Valid @RequestBody TrackTargetDTO accountDTO) {
        return trackTargetService.create(accountDTO.getName(), TrackTargetType.ACCOUNT);
    }

    @GetMapping("/{name}")
    public TrackTargetDTO getTarget(@PathVariable String name, @RequestParam(defaultValue = "ACCOUNT")
            TrackTargetType targetType) {
        return trackTargetService.getByName(name, targetType);
    }

    @GetMapping
    public Page<TrackTargetDTO> findAll(@Valid TrackTargetFilter filter, Pageable pageable) {
        return trackTargetService.findAll(filter, pageable);
    }

}
