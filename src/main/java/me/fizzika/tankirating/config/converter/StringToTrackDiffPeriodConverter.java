package me.fizzika.tankirating.config.converter;

import me.fizzika.tankirating.enums.PeriodUnit;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
public class StringToTrackDiffPeriodConverter implements WebMvcConverter<String, PeriodUnit> {

    @Override
    public PeriodUnit convert(@NotNull String source) {
        return PeriodUnit.valueOf(source.toUpperCase());
    }

}
