package me.fizzika.tankirating.mapper;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class TrackDataMapper {

    @Named("toTrackRecord")
    public TrackRecord toTrackRecord(TrackFullData model) {
        TrackRecord res = toTrackRecordInternal(model);
        res.getSupplies().forEach(s -> s.setTrack(res));
        res.getActivities().forEach(a -> a.setTrack(res));
        return res;
    }

    @Mapping(target = ".", source = "base")
    @Mapping(target = "activities", qualifiedByName = "toTrackActivityRecordList")
    @Mapping(target = "supplies", qualifiedByName = "toTrackSuppliesList")
    protected abstract TrackRecord toTrackRecordInternal(TrackFullData model);

    @Named("toTrackActivityRecordList")
    protected List<TrackActivityRecord> toTrackActivityRecordList(Map<TrackActivityType, TrackActivityData> activityMap) {
        var result = new ArrayList<TrackActivityRecord>();
        for (TrackActivityType type : activityMap.keySet()) {
            TrackActivityData model = activityMap.get(type);
            for (String entityName : model.getPlayTracks().keySet()) {
                TrackPlayData playModel = model.getPlayTracks().get(entityName);
                result.add(toTrackActivityRecord(playModel, entityName, type));
            }
        }
        return result;
    }

    @Named("toTrackSuppliesList")
    protected List<TrackSupplyRecord> toTrackSuppliesList(Map<String, TrackUsageData> usageMap) {
        return usageMap.keySet().stream()
                .map(k -> toTrackSupplyRecord(usageMap.get(k), k))
                .collect(Collectors.toList());
    }

    @Mapping(target = ".", source = "model")
    protected abstract TrackSupplyRecord toTrackSupplyRecord(TrackUsageData model, String name);

    @Mapping(target = ".", source = "playModel")
    protected abstract TrackActivityRecord toTrackActivityRecord(TrackPlayData playModel,
                                                                 String name, TrackActivityType type);


}
