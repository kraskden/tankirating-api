package me.fizzika.tankirating.service.tracking;

import me.fizzika.tankirating.dto.filter.TrackDatesFilter;
import me.fizzika.tankirating.dto.filter.TrackDiffFilter;
import me.fizzika.tankirating.dto.tracking.TrackDiffDTO;
import me.fizzika.tankirating.enums.PeriodUnit;
import me.fizzika.tankirating.enums.track.TrackFormat;

import java.util.List;

public interface TrackDiffService {

    List<TrackDiffDTO> getAllDiffsForPeriod(Integer targetId, PeriodUnit period, TrackDiffFilter diffFilter);

    TrackDiffDTO getAllTimeDiff(Integer targetId, TrackFormat format);

    TrackDiffDTO calculateDiffBetweenDates(Integer targetId, TrackDatesFilter periodFilter);

    TrackDiffDTO getDiffForPeriod(Integer targetId, PeriodUnit period, Integer offset, TrackFormat format);

}
