package me.fizzika.tankirating.service.tracking.internal.impl;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;
import static me.fizzika.tankirating.enums.track.TankiEntityType.MODULE;
import static org.apache.commons.lang3.ObjectUtils.max;

import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import me.fizzika.tankirating.enums.track.TankiEntityType;
import me.fizzika.tankirating.model.TrackData;
import me.fizzika.tankirating.model.activity.EntityNameActivityTrack;
import me.fizzika.tankirating.model.date.DatePeriod;
import me.fizzika.tankirating.model.track_data.TrackActivityData;
import me.fizzika.tankirating.model.track_data.TrackFullData;
import me.fizzika.tankirating.model.track_data.TrackPlayData;
import me.fizzika.tankirating.repository.tracking.TrackDiffRepository;
import me.fizzika.tankirating.service.tracking.internal.TrackDataDiffService;
import org.springframework.stereotype.Service;

@Service
public class TrackDataDiffServiceImpl implements TrackDataDiffService {

    @Resource
    private TrackDiffRepository trackDiffRepository;

    @Override
    public TrackFullData diff(Integer targetId, TrackFullData current, TrackFullData snapshot, DatePeriod diffDates) {
        TrackFullData result = TrackData.diff(current, snapshot);
        // Alternativa broke module stats, so trying to reuse TankiRating power
        if (isNegativeModuleDiff(result)) {
            Map<TankiEntityType, TrackActivityData> activities = result.getActivities();
            if (diffDates.getUnit() == ChronoUnit.DAYS) {
                Map<String, TrackPlayData> playTracks = activities.get(MODULE).getPlayTracks();
                playTracks.replaceAll((k, v) -> max(v, new TrackPlayData(0, 0)));
            } else {
                activities.put(MODULE, calculateModuleActivities(targetId, diffDates));
            }
        }
        return result;
    }

    private TrackActivityData calculateModuleActivities(Integer targetId, DatePeriod diffDates) {
        List<EntityNameActivityTrack> activities = trackDiffRepository.getActivityStatForAccount(targetId, diffDates.getStart(), diffDates.getEnd(), MODULE);
        Map<String, TrackPlayData> activityMap = activities.stream()
                                                           .collect(toMap(EntityNameActivityTrack::getName, t -> new TrackPlayData(t.getScore(), t.getTime())));
        return new TrackActivityData(activityMap);
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
