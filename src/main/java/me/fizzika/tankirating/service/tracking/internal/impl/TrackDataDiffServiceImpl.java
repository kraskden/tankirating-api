package me.fizzika.tankirating.service.tracking.internal.impl;

import static java.util.Optional.ofNullable;
import static me.fizzika.tankirating.enums.track.TankiEntityType.MODULE;

import java.util.Collection;
import java.util.Map;
import me.fizzika.tankirating.enums.track.TankiEntityType;
import me.fizzika.tankirating.model.TrackData;
import me.fizzika.tankirating.model.TrackSnapshot;
import me.fizzika.tankirating.model.date.DatePeriod;
import me.fizzika.tankirating.model.track_data.TrackActivityData;
import me.fizzika.tankirating.model.track_data.TrackFullData;
import me.fizzika.tankirating.service.tracking.internal.TrackDataDiffService;
import org.springframework.stereotype.Service;

@Service
public class TrackDataDiffServiceImpl implements TrackDataDiffService {

    @Override
    public TrackFullData diff(TrackSnapshot end, TrackSnapshot start) {
        return diff(start.getTargetId(), end.getTrackData(), start.getTrackData(),
                    new DatePeriod(start.getTimestamp(), end.getTimestamp(), null));
    }

    @Override
    public TrackFullData diff(Integer targetId, TrackFullData endData, TrackFullData startData, DatePeriod diffDates) {
        TrackFullData result = TrackData.diff(endData, startData);
        // Alternativa broke module stats, so assume that startData = 0
        if (isNegativeModuleDiff(result)) {
            Map<TankiEntityType, TrackActivityData> activities = result.getActivities();
            activities.put(MODULE, endData.getActivities().get(MODULE));
        }
        return result;
    }

    private boolean isNegativeModuleDiff(TrackFullData result) {
        TrackActivityData modulesStat = result.getActivities().get(MODULE);
        return ofNullable(modulesStat)
                .map(TrackActivityData::getPlayTracks)
                .map(Map::values)
                .stream()
                .flatMap(Collection::stream)
                .anyMatch(d -> d.getTime() < 0 || d.getScore() < 0);
    }
}
