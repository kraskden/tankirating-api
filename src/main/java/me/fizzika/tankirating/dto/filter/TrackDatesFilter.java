package me.fizzika.tankirating.dto.filter;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class TrackDatesFilter extends TrackFormatFilter {

    @NotNull
    private LocalDate from;

    @NotNull
    private LocalDate to;

}
