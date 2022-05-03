package me.fizzika.tankirating.mapper;

import lombok.Setter;
import me.fizzika.tankirating.dto.TrackTargetDTO;
import me.fizzika.tankirating.dto.tracking.TrackActivitiesDTO;
import me.fizzika.tankirating.dto.tracking.TrackDiffDTO;
import me.fizzika.tankirating.dto.tracking.TrackingDTO;
import me.fizzika.tankirating.mapper.prototype.TrackFormatMapper;
import me.fizzika.tankirating.record.tracking.TrackDiffRecord;
import me.fizzika.tankirating.record.tracking.TrackRecord;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

@Mapper(componentModel = "spring")
public abstract class TrackDiffMapper extends TrackFormatMapper<TrackDiffRecord, TrackDiffDTO> {

    @Setter(onMethod_ = {@Autowired})
    private TrackRecordMapper trackRecordMapper;

    @Override
    @Mapping(target = "tracking", source = "record", qualifiedByName = "toShortTrackingDTO")
    protected abstract TrackDiffDTO toShortDTO(TrackDiffRecord record, @Context TrackTargetDTO target);

    @Override
    @Mapping(target = "tracking", source = "record", qualifiedByName = "toFullTrackingDTO")
    protected abstract TrackDiffDTO toFullDTO(TrackDiffRecord record, @Context TrackTargetDTO target);


    @Named("toShortTrackingDTO")
    protected TrackingDTO toShortTrackingDTO(TrackDiffRecord record, @Context TrackTargetDTO target) {
        return trackRecordMapper.toShortDto(record.getTrackRecord(), target);
    }

    @Named("toFullTrackingDTO")
    protected TrackingDTO toFullTrackingDTO(TrackDiffRecord record, @Context TrackTargetDTO target) {
        TrackRecord trackRecord = record.getTrackRecord();
        TrackingDTO res =  trackRecordMapper.toFullDto(trackRecord, target);

        // trackRecord is null if there is no activity in the period
        // Set empty activities and supplies
        if (trackRecord == null) {
            res.setActivities(new TrackActivitiesDTO());
            res.setSupplies(new ArrayList<>());
        }
        return res;
    }

}
