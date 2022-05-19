package me.fizzika.tankirating.mapper.prototype;

import me.fizzika.tankirating.dto.TrackTargetDTO;
import me.fizzika.tankirating.enums.TrackFormat;
import me.fizzika.tankirating.exceptions.ServerException;

public abstract class TrackFormatMapper<R, D> {

    public D toDTO(R record, Integer targetId, TrackFormat format) {
        switch (format) {
            case BASE:
                return toShortDTO(record, targetId);
            case FULL:
                return toFullDTO(record, targetId);
            default:
                throw new ServerException("Unknown snapshot format").arg("format", format);
        }
    }

    protected abstract D toFullDTO(R record, Integer targetId);

    protected abstract D toShortDTO(R record, Integer targetId);

}
