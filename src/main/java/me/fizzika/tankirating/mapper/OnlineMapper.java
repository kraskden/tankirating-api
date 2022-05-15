package me.fizzika.tankirating.mapper;

import me.fizzika.tankirating.dto.alternativa.online.AlternativaOnlineDTO;
import me.fizzika.tankirating.dto.alternativa.online.AlternativaOnlineNode;
import me.fizzika.tankirating.dto.online.OnlinePcuDTO;
import me.fizzika.tankirating.dto.online.OnlineSnapshotDTO;
import me.fizzika.tankirating.model.OnlineData;
import me.fizzika.tankirating.record.online.OnlinePcuRecord;
import me.fizzika.tankirating.record.online.OnlineSnapshotRecord;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public abstract class OnlineMapper {

    public OnlineData toOnlineData(AlternativaOnlineDTO alternativaDto) {
        return alternativaDto.getNodes().values().stream()
                .map(this::toData)
                .reduce(new OnlineData(), OnlineData::mutableAdd);
    }

    public abstract OnlineSnapshotDTO toDto(OnlineSnapshotRecord record);

    public abstract OnlinePcuDTO toDto(OnlinePcuRecord record);

    protected abstract OnlineData toData(AlternativaOnlineNode node);

}
