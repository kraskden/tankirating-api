package me.fizzika.tankirating.mapper;

import me.fizzika.tankirating.dto.TrackTargetDTO;
import me.fizzika.tankirating.dto.tracking.TrackActivitiesDTO;
import me.fizzika.tankirating.dto.tracking.TrackActivityDTO;
import me.fizzika.tankirating.dto.tracking.TrackingDTO;
import me.fizzika.tankirating.enums.track.TrackActivityType;
import me.fizzika.tankirating.model.track_data.TrackActivityData;
import me.fizzika.tankirating.model.track_data.TrackFullData;
import me.fizzika.tankirating.model.track_data.TrackPlayData;
import me.fizzika.tankirating.model.track_data.TrackUsageData;
import me.fizzika.tankirating.record.tracking.TrackActivityRecord;
import me.fizzika.tankirating.record.tracking.TrackRecord;
import me.fizzika.tankirating.record.tracking.TrackSupplyRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class TrackRecordMapper {

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
           TrackActivityDTO activity = toTrackActivityDTO(rec);
           switch (rec.getType()) {
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
    protected Map<TrackActivityType, TrackActivityData> toTrackActivitiesModelMap(List<TrackActivityRecord> records) {
        Map<TrackActivityType, TrackActivityData> result = new EnumMap<>(TrackActivityType.class);
        Arrays.stream(TrackActivityType.values()).forEach(t -> result.put(t, new TrackActivityData()));
        for (TrackActivityRecord record : records) {
            result.get(record.getType()).getPlayTracks().put(record.getName(),
                    new TrackPlayData(record.getScore(), record.getTime()));
        }
        return result;
    }

    @Named("toTrackUsageModelMap")
    protected Map<String, TrackUsageData> toTrackUsageModelMap(List<TrackSupplyRecord> records) {
        return records.stream()
                .collect(Collectors.toMap(TrackSupplyRecord::getName, r -> new TrackUsageData(r.getUsages())));
    }

    protected abstract TrackActivityDTO toTrackActivityDTO(TrackActivityRecord record);

}
