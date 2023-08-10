package me.fizzika.tankirating.service.tracking.internal.impl;

import static java.util.Optional.ofNullable;
import static me.fizzika.tankirating.enums.track.TankiEntityType.MODULE;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collection;
import java.util.Map;
import me.fizzika.tankirating.model.TrackData;
import me.fizzika.tankirating.model.TrackSnapshot;
import me.fizzika.tankirating.model.date.DatePeriod;
import me.fizzika.tankirating.model.date.DateRange;
import me.fizzika.tankirating.model.track_data.TrackActivityData;
import me.fizzika.tankirating.model.track_data.TrackFullData;
import me.fizzika.tankirating.service.tracking.internal.TrackDataDiffService;
import org.springframework.stereotype.Service;

@Service
public class TrackDataDiffServiceImpl implements TrackDataDiffService {

    // Alternativa reset and after one month restore module stats.
    private static final LocalDateTime MODULE_RESET_AT = LocalDate.of(2023, Month.JULY, 7).atStartOfDay();
    private static final LocalDateTime MODULE_RESTORE_AT = LocalDate.of(2023, Month.AUGUST, 11).atStartOfDay();

    @Override
    public TrackFullData diff(TrackSnapshot end, TrackSnapshot start) {
        return diff(end.getTrackData(), start.getTrackData(),
                    new DatePeriod(start.getTimestamp(), end.getTimestamp(), null));
    }

    @Override
    public TrackFullData diff(TrackFullData endData, TrackFullData startData, DateRange dateRange) {
        LocalDateTime start = dateRange.getStart();
        LocalDateTime end = dateRange.getEnd();

        TrackFullData result = TrackData.diff(endData, startData);
        if (start.isBefore(MODULE_RESET_AT) && end.isBefore(MODULE_RESTORE_AT) && end.isAfter(MODULE_RESET_AT) && isNegativeModuleDiff(result)) {
            result.getActivities().put(MODULE, endData.getActivities().get(MODULE));
        }
        if (start.isAfter(MODULE_RESET_AT) && start.isBefore(MODULE_RESTORE_AT) && end.isAfter(MODULE_RESTORE_AT)) {
            result.getActivities().put(MODULE, new TrackActivityData());
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