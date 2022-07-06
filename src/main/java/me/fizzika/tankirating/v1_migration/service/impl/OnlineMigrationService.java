package me.fizzika.tankirating.v1_migration.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.enums.PeriodUnit;
import me.fizzika.tankirating.exceptions.ServerException;
import me.fizzika.tankirating.model.date.DatePeriod;
import me.fizzika.tankirating.record.online.OnlinePcuRecord;
import me.fizzika.tankirating.record.online.OnlineSnapshotRecord;
import me.fizzika.tankirating.repository.online.OnlinePcuRepository;
import me.fizzika.tankirating.repository.online.OnlineSnapshotRepository;
import me.fizzika.tankirating.v1_migration.record.online.OnlineDaySchema;
import me.fizzika.tankirating.v1_migration.record.online.OnlineDocument;
import me.fizzika.tankirating.v1_migration.repository.OnlineMongoRepository;
import me.fizzika.tankirating.v1_migration.service.V1MigrationService;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Profile("migration")
@Slf4j
@Order(2)
@RequiredArgsConstructor
public class OnlineMigrationService implements V1MigrationService {

    private final OnlineMongoRepository onlineMongoRepository;

    private final OnlineSnapshotRepository snapshotRepository;
    private final OnlinePcuRepository pcuRepository;

    @Override
    public void migrate() {
        OnlineDocument online = onlineMongoRepository.findFirst()
                .orElseThrow(() -> new ServerException("Online data not exists"));

        List<OnlineDaySchema> tracking = online.getDays();
        tracking.add(online.getToday());

        migrateSnapshots(tracking);
        migratePcuStat(tracking);
        log.info("Online migration is completed");
    }

    private void migrateSnapshots(List<OnlineDaySchema> tracking) {
        List<OnlineSnapshotRecord> snapshots = tracking.stream()
                .map(OnlineDaySchema::getTrack)
                .flatMap(Collection::stream)
                .map(stamp -> new OnlineSnapshotRecord(stamp.getTimestamp(), stamp.getOnline(), stamp.getInbattles()))
                .collect(Collectors.toList());

        snapshotRepository.saveAllAndFlush(snapshots);
        log.info("Successfully migrate {} online snapshots", snapshots.size());
    }

    private void migratePcuStat(List<OnlineDaySchema> tracking) {
        Map<PeriodUnit, Map<LocalDateTime, OnlinePcuRecord>> recordMap = new EnumMap<>(PeriodUnit.class);
        for (PeriodUnit period : PeriodUnit.values()) {
            recordMap.put(period, new HashMap<>());
        }

        for (OnlineDaySchema day : tracking) {
            for (PeriodUnit period : PeriodUnit.values()) {
                Map<LocalDateTime, OnlinePcuRecord> periodRecMap = recordMap.get(period);
                LocalDateTime currentDate = day.getDate().truncatedTo(ChronoUnit.DAYS);

                DatePeriod datePeriod = period.getDatePeriod(currentDate);

                if (periodRecMap.containsKey(datePeriod.getStart())) {
                    OnlinePcuRecord rec = periodRecMap.get(datePeriod.getStart());
                    rec.setOnlinePcu(Math.max(rec.getOnlinePcu(), day.getOnline().getPcu()));
                    if (day.getInbattles().getPcu() != null) {
                        if (rec.getInbattlesPcu() == null) {
                            rec.setInbattlesPcu(day.getInbattles().getPcu());
                        } else {
                            rec.setInbattlesPcu(Math.max(rec.getInbattlesPcu(), day.getInbattles().getPcu()));
                        }
                    }
                    rec.setTrackEnd(currentDate.plusDays(1));
                } else {
                    OnlinePcuRecord rec = new OnlinePcuRecord();
                    rec.setPeriod(period);
                    rec.setPeriodDates(datePeriod);
                    rec.setTrackStart(currentDate);
                    rec.setTrackEnd(currentDate.plusDays(1));
                    rec.setOnlinePcu(day.getOnline().getPcu());
                    rec.setInbattlesPcu(day.getInbattles().getPcu());
                    periodRecMap.put(datePeriod.getStart(), rec);
                }
            }
        }

        List<OnlinePcuRecord> records = recordMap.values().stream()
                .map(Map::values)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        pcuRepository.saveAllAndFlush(records);
        log.info("Successfully migrate {} online pcu records", records.size());


    }

}
