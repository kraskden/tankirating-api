package me.fizzika.tankirating.service.misc.impl;

import me.fizzika.tankirating.dto.VersionDTO;
import me.fizzika.tankirating.service.misc.VersionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;
import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

@Service
public class VersionServiceImpl implements VersionService {

    @Value("${app.version}")
    private String version;

    @DateTimeFormat(iso = DATE_TIME)
    @Value("${app.build-timestamp}")
    private LocalDate buildAt;

    @Override
    public VersionDTO getVersion() {
        return new VersionDTO(version, buildAt);
    }
}
