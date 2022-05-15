package me.fizzika.tankirating.service.tracking;

import me.fizzika.tankirating.dto.TrackTargetDTO;
import me.fizzika.tankirating.dto.filter.TrackDatesFilter;
import me.fizzika.tankirating.dto.tracking.TrackDiffDTO;
import me.fizzika.tankirating.enums.TrackFormat;
import me.fizzika.tankirating.enums.PeriodUnit;

import java.util.List;

public interface TrackDiffService {

    List<TrackDiffDTO> getAllDiffsForPeriod(TrackTargetDTO target, PeriodUnit period, TrackDatesFilter periodFilter);

    TrackDiffDTO getAllTimeDiff(TrackTargetDTO target, TrackFormat format);

    TrackDiffDTO calculateDiffBetweenDates(TrackTargetDTO target, TrackDatesFilter periodFilter);

    TrackDiffDTO getDiffForPeriod(TrackTargetDTO target, PeriodUnit period, Integer offset, TrackFormat format);

}
