package me.fizzika.tankirating.mapper;


import me.fizzika.tankirating.dto.AccountDTO;
import me.fizzika.tankirating.enums.track.TrackTargetType;
import me.fizzika.tankirating.record.tracking.TrackTargetRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountDTO toAccountDTO(TrackTargetRecord record);

    @Mapping(target = "id", ignore = true)
    TrackTargetRecord toRecord(AccountDTO dto);

}
