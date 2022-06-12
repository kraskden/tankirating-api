package me.fizzika.tankirating.controller;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.filter.OnlinePeriodFilter;
import me.fizzika.tankirating.dto.online.OnlinePcuDTO;
import me.fizzika.tankirating.dto.online.OnlineSnapshotDTO;
import me.fizzika.tankirating.enums.PeriodUnit;
import me.fizzika.tankirating.service.online.OnlineService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/online")
@RequiredArgsConstructor
@RestController
public class OnlineController {

    private final OnlineService onlineService;

    @GetMapping("/snapshot")
    public List<OnlineSnapshotDTO> getSnapshots(OnlinePeriodFilter filter) {
        return onlineService.getSnapshots(filter);
    }

    @GetMapping("/pcu/{period}")
    public List<OnlinePcuDTO> getPcuPeriodData(@PathVariable PeriodUnit period, OnlinePeriodFilter periodFilter) {
        return onlineService.getPcuPeriodData(period, periodFilter);
    }

    @GetMapping("/pcu/current")
    public List<OnlinePcuDTO> getCurrentPcu() {
        return onlineService.getCurrentPcuForAllPeriods();
    }

    @GetMapping("/pcu/{period}/{offset}")
    public OnlinePcuDTO getPcuForPeriod(@PathVariable PeriodUnit period, @PathVariable Integer offset) {
        return onlineService.getPcuForPeriod(period, offset);
    }

}
