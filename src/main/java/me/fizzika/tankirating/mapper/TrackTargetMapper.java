package me.fizzika.tankirating.mapper;

import me.fizzika.tankirating.dto.target.AccountUpdateResultDTO;
import me.fizzika.tankirating.dto.target.TrackTargetDTO;
import me.fizzika.tankirating.model.AccountUpdateResult;
import me.fizzika.tankirating.record.tracking.TrackTargetRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TrackTargetMapper {

    TrackTargetDTO toDto(TrackTargetRecord rec);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void update(TrackTargetDTO dto, @MappingTarget TrackTargetRecord record);

    AccountUpdateResultDTO toDto(AccountUpdateResult updateResult);

}
