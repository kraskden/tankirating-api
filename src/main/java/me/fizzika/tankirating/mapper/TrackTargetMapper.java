package me.fizzika.tankirating.mapper;

import me.fizzika.tankirating.dto.TrackTargetDTO;
import me.fizzika.tankirating.record.tracking.TrackTargetRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TrackTargetMapper {

    TrackTargetDTO toDto(TrackTargetRecord rec);

    @Mapping(target = "id", ignore = true)
    void update(TrackTargetDTO dto, @MappingTarget TrackTargetRecord record);

}
