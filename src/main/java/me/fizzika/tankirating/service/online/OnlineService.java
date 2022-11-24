package me.fizzika.tankirating.service.online;

import me.fizzika.tankirating.dto.filter.OnlinePeriodFilter;
import me.fizzika.tankirating.dto.online.OnlinePcuDTO;
import me.fizzika.tankirating.dto.online.OnlineSnapshotDTO;
import me.fizzika.tankirating.enums.PeriodUnit;

import java.util.List;

public interface OnlineService {

    List<OnlineSnapshotDTO> getSnapshots(OnlinePeriodFilter filter);

    List<OnlinePcuDTO> getPcuPeriodData(PeriodUnit period, OnlinePeriodFilter periodFilter);

    List<OnlinePcuDTO> getCurrentPcuForAllPeriods();

    OnlinePcuDTO getPcuForPeriod(PeriodUnit period, Integer offset);

    OnlineSnapshotDTO getLatestSnapshot();

}
