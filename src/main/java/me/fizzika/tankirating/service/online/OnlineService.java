package me.fizzika.tankirating.service.online;

import me.fizzika.tankirating.dto.filter.OnlinePeriodFilter;
import me.fizzika.tankirating.dto.online.OnlinePcuDTO;
import me.fizzika.tankirating.dto.online.OnlineSnapshotDTO;
import me.fizzika.tankirating.enums.DiffPeriodUnit;

import java.util.List;

public interface OnlineService {

    List<OnlineSnapshotDTO> getSnapshots(OnlinePeriodFilter filter);

    List<OnlinePcuDTO> getPcuPeriodData(DiffPeriodUnit period, OnlinePeriodFilter periodFilter);

    List<OnlinePcuDTO> getCurrentPcuForAllPeriods();

    OnlinePcuDTO getPcuForPeriod(DiffPeriodUnit period, Integer offset);

    OnlineSnapshotDTO getLatestSnapshot();

}