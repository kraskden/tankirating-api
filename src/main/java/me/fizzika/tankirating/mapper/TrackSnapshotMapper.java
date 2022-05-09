package me.fizzika.tankirating.mapper;

import lombok.Setter;
import me.fizzika.tankirating.dto.TrackTargetDTO;
import me.fizzika.tankirating.dto.tracking.TrackSnapshotDTO;
import me.fizzika.tankirating.dto.tracking.TrackingDTO;
import me.fizzika.tankirating.mapper.prototype.TrackFormatMapper;
import me.fizzika.tankirating.model.TrackSnapshot;
import me.fizzika.tankirating.record.tracking.TrackSnapshotRecord;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(uses = {TrackDataMapper.class, TrackRecordMapper.class}, componentModel = "spring")
public abstract class TrackSnapshotMapper extends TrackFormatMapper<TrackSnapshotRecord, TrackSnapshotDTO> {

    @Setter(onMethod_ = {@Autowired})
    private TrackRecordMapper trackRecordMapper;

    @Mapping(source = "trackData", target = "trackRecord", qualifiedByName = "toTrackRecord")
    public abstract TrackSnapshotRecord toRecord(TrackSnapshot snapshot);

    @Mapping(source = "trackRecord", target = "trackData")
    public abstract TrackSnapshot toSnapshot(TrackSnapshotRecord record);

    @Override
    @Mapping(target = "tracking", source = "record", qualifiedByName = "toShortTrackingDTO")
    protected abstract TrackSnapshotDTO toShortDTO(TrackSnapshotRecord record, @Context TrackTargetDTO target);

    @Override
    @Mapping(target = "tracking", source = "record", qualifiedByName = "toFullTrackingDTO")
    protected abstract TrackSnapshotDTO toFullDTO(TrackSnapshotRecord record, @Context TrackTargetDTO target);

    @Named("toShortTrackingDTO")
    protected TrackingDTO toShortTrackingDTO(TrackSnapshotRecord record, @Context TrackTargetDTO target) {
        return trackRecordMapper.toShortDto(record.getTrackRecord(), target);
    }

    @Named("toFullTrackingDTO")
    protected TrackingDTO toFullTrackingDTO(TrackSnapshotRecord record, @Context TrackTargetDTO target) {
        return trackRecordMapper.toFullDto(record.getTrackRecord(), target);
    }


}
