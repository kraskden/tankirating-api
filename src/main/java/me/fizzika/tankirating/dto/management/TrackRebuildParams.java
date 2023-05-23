package me.fizzika.tankirating.dto.management;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.emptyList;

@Data
public class TrackRebuildParams {

    private List<String> accounts = emptyList();
    @NotNull
    private LocalDate from;
    @NotNull
    private LocalDate to = LocalDate.now();
}
