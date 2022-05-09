package me.fizzika.tankirating.config.converter;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class StringToLocalDateConverter implements WebMvcConverter<String, LocalDate> {

    @Override
    public LocalDate convert(String source) {
        return LocalDate.parse(source);
    }

}
