package me.fizzika.tankirating.mapper;

import me.fizzika.tankirating.model.TrackSnapshot;
import me.fizzika.tankirating.record.tracking.TrackSnapshotRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {TrackDataMapper.class, TrackRecordMapper.class}, componentModel = "spring")
public interface TrackSnapshotMapper {

    @Mapping(source = "trackData", target = "trackRecord", qualifiedByName = "toTrackRecord")
    TrackSnapshotRecord toRecord(TrackSnapshot snapshot);

    @Mapping(source = "trackRecord", target = "trackData")
    TrackSnapshot toSnapshot(TrackSnapshotRecord record);

}
