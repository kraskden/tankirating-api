package me.fizzika.tankirating.dto.management;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class TrackRebuildParams {
    @NotNull
    private LocalDate from;
    @NotNull
    private LocalDate to = LocalDate.now();
}
