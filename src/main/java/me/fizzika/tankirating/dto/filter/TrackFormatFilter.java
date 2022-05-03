package me.fizzika.tankirating.dto.filter;

import lombok.Data;
import me.fizzika.tankirating.enums.TrackFormat;

import javax.validation.constraints.NotNull;

@Data
public class TrackFormatFilter {

    @NotNull
    TrackFormat format;

}
