package me.fizzika.tankirating.mapper;

import me.fizzika.tankirating.dto.TargetDTO;
import me.fizzika.tankirating.dto.tracking.TrackActivitiesDTO;
import me.fizzika.tankirating.dto.tracking.TrackActivityDTO;
import me.fizzika.tankirating.dto.tracking.TrackingDTO;
import me.fizzika.tankirating.enums.TrackActivityType;
import me.fizzika.tankirating.model.tracking.TrackActivityModel;
import me.fizzika.tankirating.model.tracking.TrackFullModel;
import me.fizzika.tankirating.model.tracking.TrackPlayModel;
import me.fizzika.tankirating.model.tracking.TrackUsageModel;
import me.fizzika.tankirating.record.tracking.TrackActivityRecord;
import me.fizzika.tankirating.record.tracking.TrackRecord;
import me.fizzika.tankirating.record.tracking.TrackSupplyRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class TrackRecordMapper {

    @Mapping(target = "premiumDays", ignore = true)
    @Mapping(target = "activities", qualifiedByName = "toTrackActivitiesDTO")
    public abstract TrackingDTO toDto(TrackRecord record, TargetDTO target);

    @Mapping(target = "premiumDays", ignore = true)
    @Mapping(target = "activities", ignore = true)
    @Mapping(target = "supplies", ignore = true)
    public abstract TrackingDTO toShortDto(TrackRecord record);

    @Mapping(target = "base", source = ".")
    @Mapping(target = "activities", qualifiedByName = "toTrackActivitiesModelMap")
    @Mapping(target = "supplies", qualifiedByName = "toTrackUsageModelMap")
    public abstract TrackFullModel toModel(TrackRecord record);

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
    protected Map<TrackActivityType, TrackActivityModel> toTrackActivitiesModelMap(List<TrackActivityRecord> records) {
        Map<TrackActivityType, TrackActivityModel> result = new EnumMap<>(TrackActivityType.class);
        Arrays.stream(TrackActivityType.values()).forEach(t -> result.put(t, new TrackActivityModel()));
        for (TrackActivityRecord record : records) {
            result.get(record.getType()).getPlayTracks().put(record.getName(),
                    new TrackPlayModel(record.getScore(), record.getTime()));
        }
        return result;
    }

    @Named("toTrackUsageModelMap")
    protected Map<String, TrackUsageModel> toTrackUsageModelMap(List<TrackSupplyRecord> records) {
        return records.stream()
                .collect(Collectors.toMap(TrackSupplyRecord::getName, r -> new TrackUsageModel(r.getUsages())));
    }

    protected abstract TrackActivityDTO toTrackActivityDTO(TrackActivityRecord record);

}
