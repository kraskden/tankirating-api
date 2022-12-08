package me.fizzika.tankirating.dto.filter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class TrackDatesFilter extends TrackFormatFilter {

    @NotNull
    @Schema(example = "2021-11-15")
    private LocalDate from;

    @NotNull
    @Schema(example = "2022-11-14")
    private LocalDate to;

}
