package me.fizzika.tankirating.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class VersionDTO {
    private String version;
    private LocalDate buildAt;
}
