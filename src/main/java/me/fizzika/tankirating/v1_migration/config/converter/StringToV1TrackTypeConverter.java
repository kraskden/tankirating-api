package me.fizzika.tankirating.v1_migration.config.converter;

import me.fizzika.tankirating.v1_migration.enums.V1TrackType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class StringToV1TrackTypeConverter implements Converter<String, V1TrackType> {

    @Override
    public V1TrackType convert(String source) {
        return V1TrackType.valueOf(source.toUpperCase());
    }

}
