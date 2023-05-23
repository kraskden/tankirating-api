package me.fizzika.tankirating.controller;

import me.fizzika.tankirating.dto.VersionDTO;
import me.fizzika.tankirating.service.misc.VersionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
