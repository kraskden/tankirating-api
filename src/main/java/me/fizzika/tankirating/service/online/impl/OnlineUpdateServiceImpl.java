package me.fizzika.tankirating.service.online.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.enums.PeriodUnit;
import me.fizzika.tankirating.mapper.OnlineMapper;
import me.fizzika.tankirating.model.OnlineData;
import me.fizzika.tankirating.model.date.DatePeriod;
import me.fizzika.tankirating.record.online.OnlinePcuRecord;
import me.fizzika.tankirating.record.online.OnlineSnapshotRecord;
import me.fizzika.tankirating.repository.online.OnlinePcuRepository;
import me.fizzika.tankirating.repository.online.OnlineSnapshotRepository;
import me.fizzika.tankirating.service.online.AlternativaOnlineService;
import me.fizzika.tankirating.service.online.OnlineUpdateService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class OnlineUpdateServiceImpl implements OnlineUpdateService {

    private final AlternativaOnlineService alternativaService;
    private final OnlineMapper alternativaMapper;

    private final OnlineSnapshotRepository snapshotRepository;
    private final OnlinePcuRepository pcuRepository;

    @Override
    @Scheduled(cron = "${app.cron.online-updating}")
    public void updateOnline() {
        OnlineData data = alternativaService.getOnlineData()
                .map(alternativaMapper::toOnlineData)
                .filter(OnlineData::valid)
                .orElse(null);
        if (data == null) {
            log.error("Cannot update online data, check alternativa API");
            return;
        }

        createSnapshot(data);
        updatePcuStats(data);
        log.info("Online has been updated: [{}, {}]", data.getOnline(), data.getInbattles());
    }

    private void createSnapshot(OnlineData data) {
        snapshotRepository.save(new OnlineSnapshotRecord(LocalDateTime.now(), data.getOnline(), data.getInbattles()));
    }

    private void updatePcuStats(OnlineData data) {
        LocalDateTime now = LocalDateTime.now();
        for (PeriodUnit period : PeriodUnit.values()) {
            DatePeriod datePeriod = period.getDatePeriod(now);
            OnlinePcuRecord rec = pcuRepository.findFirstByPeriodAndPeriodStart(period, datePeriod.getStart())
                    .orElseGet(() -> newRecord(data, period, datePeriod));
            rec.setTrackStart(rec.getTrackStart() != null ? rec.getTrackStart() : now);
            rec.setTrackEnd(now);
            rec.setInbattlesPcu(Math.max(rec.getInbattlesPcu(), data.getInbattles()));
            rec.setOnlinePcu(Math.max(rec.getOnlinePcu(), data.getOnline()));
            pcuRepository.save(rec);
        }
    }

    private OnlinePcuRecord newRecord(OnlineData data, PeriodUnit period, DatePeriod datePeriod) {
        var res = new OnlinePcuRecord();
        res.setPeriod(period);
        res.setPeriodDates(datePeriod);
        res.setOnlinePcu(data.getOnline());
        res.setInbattlesPcu(data.getInbattles());
        return res;
    }

}
