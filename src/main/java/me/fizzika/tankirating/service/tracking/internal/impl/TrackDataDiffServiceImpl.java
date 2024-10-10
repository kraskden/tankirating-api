package me.fizzika.tankirating.service.tracking.internal.impl;

import static java.util.Optional.ofNullable;
import static me.fizzika.tankirating.enums.track.TankiEntityType.MODE;
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

    // Alternativa broke modes statistics. Кринжули
    private static final LocalDateTime MODE_BROKED_AT = LocalDate.of(2024, Month.APRIL, 28).atStartOfDay();

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
        boolean negativeModuleDiff = isNegativeModuleDiff(result);

        if (start.isBefore(MODULE_RESET_AT) && end.isBefore(MODULE_RESTORE_AT) && end.isAfter(MODULE_RESET_AT) && negativeModuleDiff) {
            result.getActivities().put(MODULE, endData.getActivities().get(MODULE));
        }
        if (negativeModuleDiff) {
            result.getActivities().put(MODULE, endData.getActivities().get(MODULE));
        }

        // TODO: Waiting for alternativa fixes...
        if (end.isAfter(MODE_BROKED_AT)) {
            result.getActivities().put(MODE, new TrackActivityData());
        }
        return result;
    }

    @Override
    public boolean strictDiffMode(DatePeriod datePeriod) {
        return datePeriod.getStart().isAfter(MODE_BROKED_AT);
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