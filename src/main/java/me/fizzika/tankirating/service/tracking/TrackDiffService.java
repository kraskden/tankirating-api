package me.fizzika.tankirating.service.tracking;

import me.fizzika.tankirating.dto.filter.TrackDatesFilter;
import me.fizzika.tankirating.dto.filter.TrackDiffFilter;
import me.fizzika.tankirating.dto.tracking.TrackDiffDTO;
import me.fizzika.tankirating.enums.TrackFormat;
import me.fizzika.tankirating.enums.PeriodUnit;

import java.util.List;

public interface TrackDiffService {

    List<TrackDiffDTO> getAllDiffsForPeriod(Integer targetId, PeriodUnit period, TrackDiffFilter diffFilter);

    TrackDiffDTO getAllTimeDiff(Integer targetId, TrackFormat format);

    TrackDiffDTO calculateDiffBetweenDates(Integer targetId, TrackDatesFilter periodFilter);

    TrackDiffDTO getDiffForPeriod(Integer targetId, PeriodUnit period, Integer offset, TrackFormat format);

}
