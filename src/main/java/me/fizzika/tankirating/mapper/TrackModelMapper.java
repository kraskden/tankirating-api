package me.fizzika.tankirating.mapper;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class TrackModelMapper {

    @Mapping(target = ".", source = "base")
    @Mapping(target = "activities", qualifiedByName = "toTrackActivityRecordList")
    @Mapping(target = "supplies", qualifiedByName = "toTrackSuppliesList")
    public abstract TrackRecord toTrackRecord(TrackFullModel model);

    @Named("toTrackActivityRecordList")
    protected List<TrackActivityRecord> toTrackActivityRecordList(Map<TrackActivityType, TrackActivityModel> activityMap) {
        var result = new ArrayList<TrackActivityRecord>();
        for (TrackActivityType type : activityMap.keySet()) {
            TrackActivityModel model = activityMap.get(type);
            for (String entityName : model.getPlayTracks().keySet()) {
                TrackPlayModel playModel = model.getPlayTracks().get(entityName);
                result.add(toTrackActivityRecord(playModel, entityName, type));
            }
        }
        return result;
    }

    @Named("toTrackSuppliesList")
    protected List<TrackSupplyRecord> toTrackSuppliesList(Map<String, TrackUsageModel> usageMap) {
        return usageMap.keySet().stream()
                .map(k -> toTrackSupplyRecord(usageMap.get(k), k))
                .collect(Collectors.toList());
    }

    @Mapping(target = ".", source = "model")
    protected abstract TrackSupplyRecord toTrackSupplyRecord(TrackUsageModel model, String name);

    @Mapping(target = ".", source = "playModel")
    protected abstract TrackActivityRecord toTrackActivityRecord(TrackPlayModel playModel,
                                                                 String name, TrackActivityType type);


}
