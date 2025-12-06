package me.fizzika.tankirating.config.converter;

import me.fizzika.tankirating.enums.DiffPeriodUnit;
import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotNull;

@Component
public class StringToPeriodUnitConverter implements WebMvcConverter<String, DiffPeriodUnit> {

    @Override
    public DiffPeriodUnit convert(@NotNull String source) {
        return DiffPeriodUnit.valueOf(source.toUpperCase());
    }

}