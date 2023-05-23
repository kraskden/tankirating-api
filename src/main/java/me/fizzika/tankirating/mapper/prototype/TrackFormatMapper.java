package me.fizzika.tankirating.mapper.prototype;

import me.fizzika.tankirating.enums.track.TrackFormat;
import me.fizzika.tankirating.exceptions.ServerException;

public abstract class TrackFormatMapper<R, D> {

    public D toDTO(R record, Integer targetId, TrackFormat format) {
        return switch (format) {
            case BASE -> toShortDTO(record, targetId);
            case FULL -> toFullDTO(record, targetId);
        };
    }

    protected abstract D toFullDTO(R record, Integer targetId);

    protected abstract D toShortDTO(R record, Integer targetId);

}
