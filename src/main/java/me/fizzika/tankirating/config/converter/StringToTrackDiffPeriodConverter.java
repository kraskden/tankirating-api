package me.fizzika.tankirating.config.converter;

import me.fizzika.tankirating.enums.track.TrackDiffPeriod;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
public class StringToTrackDiffPeriodConverter implements WebMvcConverter<String, TrackDiffPeriod> {

    @Override
    public TrackDiffPeriod convert(@NotNull String source) {
        return TrackDiffPeriod.valueOf(source.toUpperCase());
    }

}
