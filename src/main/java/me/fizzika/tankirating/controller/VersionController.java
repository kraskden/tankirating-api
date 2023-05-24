package me.fizzika.tankirating.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import me.fizzika.tankirating.dto.VersionDTO;
import me.fizzika.tankirating.service.misc.VersionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Tag(name = "Version", description = "Version API")
@RestController
@RequestMapping("/version")
public class VersionController {

    @Resource
    private VersionService versionService;

    @GetMapping
    public VersionDTO getVersion() {
        return versionService.getVersion();
    }
}
