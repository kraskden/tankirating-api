package me.fizzika.tankirating.v1_migration.service.impl.account.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.dto.target.TrackTargetDTO;
import me.fizzika.tankirating.enums.PeriodUnit;
import me.fizzika.tankirating.enums.track.TankiSupply;
import me.fizzika.tankirating.enums.track.TrackTargetStatus;
import me.fizzika.tankirating.enums.track.TrackTargetType;
import me.fizzika.tankirating.mapper.TrackDataMapper;
import me.fizzika.tankirating.mapper.TrackRecordMapper;
import me.fizzika.tankirating.model.date.DatePeriod;
import me.fizzika.tankirating.model.track_data.TrackFullData;
import me.fizzika.tankirating.record.tracking.TrackDiffRecord;
import me.fizzika.tankirating.record.tracking.TrackRecord;
import me.fizzika.tankirating.record.tracking.TrackSnapshotRecord;
import me.fizzika.tankirating.record.tracking.TrackTargetRecord;
import me.fizzika.tankirating.repository.tracking.TrackDiffRepository;
import me.fizzika.tankirating.repository.tracking.TrackSnapshotRepository;
import me.fizzika.tankirating.service.tracking.target.TrackTargetService;
import me.fizzika.tankirating.service.tracking.internal.TrackStoreService;
import me.fizzika.tankirating.v1_migration.mapper.TrackingSchemaMapper;
import me.fizzika.tankirating.v1_migration.record.tracking.AccountDocument;
import me.fizzika.tankirating.v1_migration.record.tracking.TrackSupplySchema;
import me.fizzika.tankirating.v1_migration.record.tracking.TrackingSchema;
import me.fizzika.tankirating.v1_migration.service.impl.account.AccountMigrationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Profile("migration")
@Slf4j
@RequiredArgsConstructor
public class AccountMigrationRunnerImpl implements AccountMigrationRunner {

    private final TrackTargetService trackTargetService;
    private final TrackStoreService trackStoreService;

    private final TrackingSchemaMapper schemaMapper;
    private final TrackRecordMapper recordMapper;
    private final TrackDataMapper dataMapper;

    private final TrackSnapshotRepository snapshotRepository;
    private final TrackDiffRepository diffRepository;

    @Override
    @Async("migrationTaskExecutor")
    public CompletableFuture<Void> migrateAccountAsync(AccountDocument account) {
        String login = account.getLogin();
        log.info("Starting migration for {}", login);

        TrackTargetDTO target = trackTargetService.getOptionalByName(login, TrackTargetType.ACCOUNT)
                .orElseGet(() -> createAccount(login));

        TreeMap<LocalDateTime, TrackingSchema> v1Snapshots = account.getTracking().stream()
                .peek(this::fixTrackingSchema)
                .collect(Collectors.toMap(TrackingSchema::getTimestamp, Function.identity(), (x, y) -> x, TreeMap::new));

        if (!v1Snapshots.isEmpty()) {
            migrateSnapshots(account, v1Snapshots, target);
            migrateDiffs(account, target);
            createYearlyDiffs(target, v1Snapshots);
            createAllTimeDiff(target, v1Snapshots.firstEntry().getValue(), v1Snapshots.lastEntry().getValue());
        }

        log.info("Complete migration for {}", login);
        return CompletableFuture.completedFuture(null);
    }

    /**
     * <p> Migrate snapshots from V1. </p>
     * <p> There are two key features: </p>
     *
     * <p>1. Snapshots are not stored for every day in the V1 system, only for "major" dates, such as start of the week or
     * start of the month. But our system is store snapshot for every day for more precisely calculations.
     * Anyway, V1 stores DAY diffs between two snapshots. So we can restore missing snapshots by getting snapshot and
     * the diff for next day: <code>Snapshot(day) = Snapshot(day - 1) + Diff(day)</code></p>
     *
     * <p>2. There is no need to create TrackRecord for each snapshot: if snapshot is the same as prevDay snapshot,
     * then we can reuse prevDay TrackRecord. Two snapshots are the same if both contains same time and premium fields.
     *
     * </p>
     */
    private void migrateSnapshots(AccountDocument account,
                                  TreeMap<LocalDateTime, TrackingSchema> v1Snapshots,
                                  TrackTargetDTO target) {
        TreeMap<LocalDateTime, TrackingSchema> dailyDiffs = account.getDaily().stream()
                .peek(this::fixTrackingSchema)
                .collect(Collectors.toMap(TrackingSchema::getTimestamp, Function.identity(), (x, y) -> x, TreeMap::new));

        List<TrackSnapshotRecord> snapshots = new ArrayList<>();
        List<TrackingSchema> brokenSnapshots = new ArrayList<>();

        TrackSnapshotRecord prevSnapshot = null;
        for (LocalDateTime today = v1Snapshots.firstKey();
             !today.isAfter(v1Snapshots.lastKey()); today = today.plusDays(1)) {
            TrackSnapshotRecord currSnapshot = null;

            // Today snapshot is already exists in the V1
            if (v1Snapshots.containsKey(today)) {

                TrackingSchema v1Snapshot = v1Snapshots.get(today);
                if (v1Snapshot.getTime() == null || v1Snapshot.getTime().equals(0)) {
                    // Snapshot is broken (as much broken as V1 is)
                    brokenSnapshots.add(v1Snapshot);
                    prevSnapshot = null;
                    continue;
                }

                /* If today snapshot is the same as snapshot for previous day,
                   then don't create new track data record in the database - reuse record from prevSnapshot
                */
                boolean snapshotIsNotChanged = prevSnapshot != null &&
                        v1Snapshot.getTime() == prevSnapshot.getTrackRecord().getTime();

                currSnapshot = snapshotIsNotChanged ?
                        new TrackSnapshotRecord(prevSnapshot.getTrackRecord(), today, prevSnapshot.getTarget(),
                                v1Snapshot.getHasPremium())
                        : toSnapshot(v1Snapshot, target);

            } else if (dailyDiffs.containsKey(today) && prevSnapshot != null) {
                TrackingSchema dayDiff = dailyDiffs.get(today);
                if (dayDiff.getTime() == null || dayDiff.getTime() == 0) {
                    currSnapshot = new TrackSnapshotRecord(prevSnapshot.getTrackRecord(), today, prevSnapshot.getTarget(),
                            dayDiff.getHasPremium());
                } else {
                    TrackFullData currSnapshotData = schemaMapper.toDataModel(dayDiff);
                    currSnapshotData.add(recordMapper.toModel(prevSnapshot.getTrackRecord()));
                    TrackRecord currTrackRecord = dataMapper.toTrackRecord(currSnapshotData);
                    currSnapshot = new TrackSnapshotRecord(currTrackRecord, today, prevSnapshot.getTarget(),
                            dayDiff.getHasPremium());
                }
            }

            if (currSnapshot != null) {
                snapshots.add(currSnapshot);
            }
            prevSnapshot = currSnapshot;
        }

        if (!brokenSnapshots.isEmpty()) {
            log.warn("Migration [{}]: Found {} broken snapshots: {}", account.getLogin(),
                    brokenSnapshots.size(),
                    brokenSnapshots.stream()
                            .map(TrackingSchema::getTimestamp)
                            .map(LocalDateTime::toString)
                            .collect(Collectors.joining(","))
            );
        }

        snapshotRepository.saveAllAndFlush(snapshots);
        log.info("Migration [{}]: Successfully migrate {} snapshots", account.getLogin(), snapshots.size());
    }


    private void migrateDiffs(AccountDocument account, TrackTargetDTO target) {
        migrateDiffs(account.getDaily(), PeriodUnit.DAY, target);

        if (account.getCurrWeek() != null) {
            account.getWeekly().add(account.getCurrWeek());
        }
        migrateDiffs(account.getWeekly(), PeriodUnit.WEEK, target);

        if (account.getCurrMonth() != null) {
            account.getMonthly().add(account.getCurrMonth());
        }
        migrateDiffs(account.getMonthly(), PeriodUnit.MONTH, target);
    }

    private void createYearlyDiffs(TrackTargetDTO target, TreeMap<LocalDateTime, TrackingSchema> v1Snapshots) {
        int firstYear = v1Snapshots.firstKey().getYear();
        int lastYear = v1Snapshots.lastKey().getYear();
        for (int year = firstYear; year <= lastYear; year++) {
            LocalDateTime yearStart = LocalDate.ofYearDay(year, 1).atStartOfDay();
            LocalDateTime yearEnd = LocalDate.ofYearDay(year + 1, 1).atStartOfDay();

            var firstTrackingEntry = v1Snapshots.ceilingEntry(yearStart);
            var lastTrackingEntry = v1Snapshots.floorEntry(yearEnd);
            if (firstTrackingEntry != null && lastTrackingEntry != null) {
                createDiff(target, firstTrackingEntry.getValue(), lastTrackingEntry.getValue(),
                        PeriodUnit.YEAR, new DatePeriod(yearStart, yearEnd, ChronoUnit.YEARS));
            } else {
                log.warn("Migration [{}]: Cannot create diff for year {}", target.getName(), year);
            }
        }
        log.info("Migration [{}]: Successfully created YEAR diffs", target.getName());
    }

    private void createAllTimeDiff(TrackTargetDTO target, TrackingSchema initSnapshot, TrackingSchema lastSnapshot) {
        createDiff(target, initSnapshot, lastSnapshot, PeriodUnit.ALL_TIME,
                PeriodUnit.ALL_TIME.getDatePeriod(LocalDateTime.now()));
        log.info("Migration [{}]: Successfully created ALL_TIME diff", target.getName());
    }

    private void createDiff(TrackTargetDTO target, TrackingSchema initSnapshot, TrackingSchema lastSnapshot,
                            PeriodUnit period, DatePeriod periodDates) {
        TrackFullData diffData = schemaMapper.toDataModel(lastSnapshot);
        diffData.sub(schemaMapper.toDataModel(initSnapshot));

        if (diffData.getBase().getTime() < 0) {
            // Account is broken, likely the nickname has been changed and new account with this nickname has been registered
            if (target.getStatus() != TrackTargetStatus.DISABLED) {
                log.warn("Found broken account (diffTime < 0): {}. Consider disabling it", target.getName());
                target.setStatus(TrackTargetStatus.DISABLED);
                trackTargetService.update(target.getId(), target);
            }
            log.warn("Skip broken {} diff for account {}", period, target.getName());
            return;
        }

        TrackDiffRecord record = new TrackDiffRecord();
        record.setTarget(new TrackTargetRecord(target.getId()));
        record.setTrackRecord(dataMapper.toTrackRecord(diffData));
        record.setPeriod(period);

        fillDiffRecordDates(record, periodDates);
        record.setTrackStart(initSnapshot.getTimestamp());
        record.setTrackEnd(lastSnapshot.getTimestamp());

        record.setMaxScore(lastSnapshot.getScore());
        record.setPremiumDays(snapshotRepository.getPremiumDays(target.getId(),
                periodDates.getStart(), periodDates.getEnd()));

        diffRepository.save(record);
    }

    private void migrateDiffs(List<TrackingSchema> schemas, PeriodUnit period, TrackTargetDTO target) {
        List<TrackDiffRecord> records = schemas.stream()
                .peek(this::fixTrackingSchema)
                .filter(this::isValidDiffSchema)
                .map(schema -> toDiff(schema, period, target))
                .collect(Collectors.toList());
        diffRepository.saveAllAndFlush(records);
        log.info("Migration [{}]: Successfully migrate {} diffs for {} period", target.getName(), records.size(), period);
    }

    private TrackDiffRecord toDiff(TrackingSchema diff, PeriodUnit period, TrackTargetDTO target) {
        TrackDiffRecord res = new TrackDiffRecord();
        res.setTarget(new TrackTargetRecord(target.getId()));
        res.setTrackRecord(schemaMapper.toRecord(diff));
        res.setPeriod(period);

        DatePeriod datePeriod = period.getDatePeriod(diff.getTimestamp());
        fillDiffRecordDates(res, datePeriod);

        res.setMaxScore(snapshotRepository.findLastSnapshot(target.getId(), datePeriod.getStart(), datePeriod.getEnd())
                .map(s -> s.getTrackRecord().getScore())
                .orElse(null));

        res.setPremiumDays(period == PeriodUnit.DAY ?
                diff.getHasPremium() ? 1 : 0
                : snapshotRepository.getPremiumDays(target.getId(), datePeriod.getStart(), datePeriod.getEnd()));
        return res;
    }


    private void fillDiffRecordDates(TrackDiffRecord mappingTarget, DatePeriod period) {
        mappingTarget.setPeriodStart(period.getStart());
        mappingTarget.setPeriodEnd(period.getEnd());
        mappingTarget.setTrackStart(period.getStart());
        mappingTarget.setTrackEnd(period.getEnd());
    }

    private TrackSnapshotRecord toSnapshot(TrackingSchema snapshot, TrackTargetDTO target) {
        TrackSnapshotRecord res = new TrackSnapshotRecord();
        res.setTarget(new TrackTargetRecord(target.getId()));
        res.setTimestamp(snapshot.getTimestamp());
        res.setTrackRecord(schemaMapper.toRecord(snapshot));
        res.setHasPremium(snapshot.getHasPremium());
        return res;
    }

    private void fixTrackingSchema(TrackingSchema schema) {
        schema.getSupplies().removeIf(supplySchema -> supplySchema.getName() == null);
        schema.getSupplies().add(new TrackSupplySchema(TankiSupply.NUCLEAR.name(), 0));
        schema.setTimestamp(schema.getTimestamp()
                .minus(3, ChronoUnit.HOURS)
                .plusDays(1)
        );
        if (schema.getTime() == null) {
            schema.setTime(0);
        }
    }

    // Some diffs in the V1 system is broken
    // Oh sh, here we go again
    private boolean isValidDiffSchema(TrackingSchema schema) {
        if (schema.getTime() != null && schema.getTime().equals(0)) {
            return true;
        }

        if (schema.getActivities() == null || schema.getTime() == null || schema.getTime() < 0) {
            return false;
        }

        for (var activity : schema.getActivities()) {
            if (activity.getTime() == null || activity.getTime() < 0) {
                return false;
            }
        }

        for (var supply : schema.getSupplies()) {
            if (supply.getCount() == null || supply.getCount() < 0) {
                return false;
            }
        }

        return true;
    }

    private TrackTargetDTO createAccount(String login) {
        var res = trackTargetService.create(login, TrackTargetType.ACCOUNT);
        log.info("Migration: Create account {}", login);
        return res;
    }

}
