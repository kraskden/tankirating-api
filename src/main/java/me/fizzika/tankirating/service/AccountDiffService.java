package me.fizzika.tankirating.service;

import me.fizzika.tankirating.dto.filter.TrackDatesFilter;
import me.fizzika.tankirating.dto.tracking.TrackDiffDTO;
import me.fizzika.tankirating.enums.TrackFormat;
import me.fizzika.tankirating.enums.track.TrackDiffPeriod;

import java.util.List;

public interface AccountDiffService {

    List<TrackDiffDTO> getAllDiffsForPeriod(String nickname, TrackDiffPeriod period, TrackDatesFilter periodFilter);

    TrackDiffDTO getAllTimeDiff(String nickname, TrackFormat format);

    TrackDiffDTO calculateDiffBetweenDates(String nickname, TrackDatesFilter periodFilter);

    TrackDiffDTO getDiffForPeriod(String nickname, TrackDiffPeriod period, Integer offset, TrackFormat format);

}
