package me.fizzika.tankirating.mapper;

import me.fizzika.tankirating.dto.TrackTargetDTO;
import me.fizzika.tankirating.record.tracking.TrackTargetRecord;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrackTargetMapper {

    TrackTargetDTO toDto(TrackTargetRecord rec);

}
