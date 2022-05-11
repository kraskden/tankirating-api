package me.fizzika.tankirating.mapper;

import lombok.Setter;
import me.fizzika.tankirating.dto.TrackTargetDTO;
import me.fizzika.tankirating.dto.tracking.*;
import me.fizzika.tankirating.enums.track.TankiEntityType;
import me.fizzika.tankirating.model.track_data.TrackActivityData;
import me.fizzika.tankirating.model.track_data.TrackFullData;
import me.fizzika.tankirating.model.track_data.TrackPlayData;
import me.fizzika.tankirating.model.track_data.TrackUsageData;
import me.fizzika.tankirating.record.tracking.TrackActivityRecord;
import me.fizzika.tankirating.record.tracking.TrackRecord;
import me.fizzika.tankirating.record.tracking.TrackUsageRecord;
import me.fizzika.tankirating.service.tracking.internal.TrackEntityService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class TrackRecordMapper {

    @Setter(onMethod_ = {@Autowired})
    protected TrackEntityService entityService;

    @Mapping(target = "premiumDays", ignore = true)
    @Mapping(target = "activities", qualifiedByName = "toTrackActivitiesDTO")
    public abstract TrackingDTO toFullDto(TrackRecord record, TrackTargetDTO target);

    @Mapping(target = "premiumDays", ignore = true)
    @Mapping(target = "activities", ignore = true)
    @Mapping(target = "supplies", ignore = true)
    public abstract TrackingDTO toShortDto(TrackRecord record, TrackTargetDTO target);

    @Mapping(target = "base", source = ".")
    @Mapping(target = "activities", qualifiedByName = "toTrackActivitiesModelMap")
    @Mapping(target = "supplies", qualifiedByName = "toTrackUsageModelMap")
    public abstract TrackFullData toModel(TrackRecord record);

    @Named("toTrackActivitiesDTO")
    protected TrackActivitiesDTO toTrackActivitiesDTO(List<TrackActivityRecord> activityRecords) {
       TrackActivitiesDTO activities = new TrackActivitiesDTO();
       for (TrackActivityRecord rec : activityRecords) {
           TrackEntityDTO entity = entityService.get(rec.getEntityId());

           TrackActivityDTO activity = toTrackActivityDTO(rec, entity);

           switch (entity.getType()) {
               case HULL:
                   activities.getHulls().add(activity);
                   break;
               case MODE:
                   activities.getModes().add(activity);
                   break;
               case MODULE:
                   activities.getModules().add(activity);
                   break;
               case TURRET:
                   activities.getTurrets().add(activity);
                   break;
           }
       }
       return activities;
    }

    @Named("toTrackActivitiesModelMap")
    protected Map<TankiEntityType, TrackActivityData> toTrackActivitiesModelMap(List<TrackActivityRecord> records) {
        Map<TankiEntityType, TrackActivityData> result = new EnumMap<>(TankiEntityType.class);
        Arrays.stream(TankiEntityType.values()).forEach(t -> result.put(t, new TrackActivityData()));
        for (TrackActivityRecord record : records) {
            TrackEntityDTO entity = entityService.get(record.getEntityId());
            result.get(entity.getType()).getPlayTracks().put(entity.getName(),
                    new TrackPlayData(record.getScore(), record.getTime()));
        }
        return result;
    }

    @Named("toTrackUsageModelMap")
    protected Map<String, TrackUsageData> toTrackUsageModelMap(List<TrackUsageRecord> records) {
        return records.stream()
                .collect(Collectors.toMap(r -> entityService.get(r.getEntityId()).getName(),
                        r -> new TrackUsageData(r.getUsages())));
    }

    protected abstract TrackActivityDTO toTrackActivityDTO(TrackActivityRecord record, TrackEntityDTO entity);

    protected TrackUsageDTO toSupplyDTO(TrackUsageRecord record) {
        TrackUsageDTO res = new TrackUsageDTO();
        res.setName(entityService.get(record.getEntityId()).getName());
        res.setUsages(record.getUsages());
        return res;
    }

}
