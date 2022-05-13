package me.fizzika.tankirating.v1_migration.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.dto.TrackTargetDTO;
import me.fizzika.tankirating.enums.track.TankiSupply;
import me.fizzika.tankirating.enums.track.TrackDiffPeriod;
import me.fizzika.tankirating.enums.track.TrackTargetType;
import me.fizzika.tankirating.mapper.TrackDataMapper;
import me.fizzika.tankirating.mapper.TrackRecordMapper;
import me.fizzika.tankirating.model.DatePeriod;
import me.fizzika.tankirating.model.track_data.TrackFullData;
import me.fizzika.tankirating.record.tracking.TrackDiffRecord;
import me.fizzika.tankirating.record.tracking.TrackRecord;
import me.fizzika.tankirating.record.tracking.TrackSnapshotRecord;
import me.fizzika.tankirating.record.tracking.TrackTargetRecord;
import me.fizzika.tankirating.repository.TrackDiffRepository;
import me.fizzika.tankirating.repository.TrackSnapshotRepository;
import me.fizzika.tankirating.service.tracking.TrackTargetService;
import me.fizzika.tankirating.v1_migration.mapper.TrackingSchemaMapper;
import me.fizzika.tankirating.v1_migration.record.tracking.AccountDocument;
import me.fizzika.tankirating.v1_migration.record.tracking.TrackSupplySchema;
import me.fizzika.tankirating.v1_migration.record.tracking.TrackingSchema;
import me.fizzika.tankirating.v1_migration.repository.AccountMongoRepository;
import me.fizzika.tankirating.v1_migration.repository.AccountMongoTemplateRepository;
import me.fizzika.tankirating.v1_migration.service.V1MigrationService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountMigrationService implements V1MigrationService {

    private final AccountMongoRepository mongoRepository;
    private final AccountMongoTemplateRepository mongoTemplateRepository;

    private final TrackTargetService trackTargetService;

    private final TrackingSchemaMapper schemaMapper;
    private final TrackRecordMapper recordMapper;
    private final TrackDataMapper dataMapper;

    private final TrackSnapshotRepository snapshotRepository;
    private final TrackDiffRepository diffRepository;

    @Override
    @PostConstruct
    public void migrate() {
        List<String> logins = mongoTemplateRepository.getAccountLogins();
        log.info("Found {} accounts", logins.size());
        for (String login : logins) {
            log.info("Starting migration for {}", login);
            migrateAccount(mongoRepository.findAccountDocumentByLogin(login));
            log.info("Complete migration for {}", login);
        }
    }

    private void migrateAccount(AccountDocument account) {
        String login = account.getLogin();

        TrackTargetDTO target = trackTargetService.getOptionalByName(login, TrackTargetType.ACCOUNT)
                .orElseGet(() -> createAccount(login));

        migrateSnapshots(account, target);
        migrateDiffs(account, target);
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
     *
     */
    private void migrateSnapshots(AccountDocument account, TrackTargetDTO target) {
        TreeMap<LocalDateTime, TrackingSchema> v1Snapshots = account.getTracking().stream()
                .peek(this::fixTrackingSchema)
                .collect(Collectors.toMap(TrackingSchema::getTimestamp, Function.identity(), (x, y) -> x, TreeMap::new));

        if (v1Snapshots.isEmpty()) {
            return;
        }

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
                        v1Snapshot.getTime() == prevSnapshot.getTrackRecord().getTime() &&
                        premiumMatches(v1Snapshot, prevSnapshot.getTrackRecord());

                currSnapshot = snapshotIsNotChanged ?
                        new TrackSnapshotRecord(prevSnapshot.getTrackRecord(), today, prevSnapshot.getTarget())
                        : toSnapshot(v1Snapshot, target);

            } else if (dailyDiffs.containsKey(today) &&  prevSnapshot != null) {
                TrackingSchema dayDiff = dailyDiffs.get(today);
                if (dayDiff.getTime() == null || (
                        dayDiff.getTime() == 0 && premiumMatches(dayDiff, prevSnapshot.getTrackRecord()))) {
                    currSnapshot = new TrackSnapshotRecord(prevSnapshot.getTrackRecord(), today, prevSnapshot.getTarget());
                } else {
                    TrackFullData currSnapshotData = schemaMapper.toDataModel(dayDiff);
                    currSnapshotData.add(recordMapper.toModel(prevSnapshot.getTrackRecord()));
                    TrackRecord currTrackRecord = dataMapper.toTrackRecord(currSnapshotData, dayDiff.getHasPremium() ? 1 : 0);
                    currSnapshot = new TrackSnapshotRecord(currTrackRecord, today, prevSnapshot.getTarget());
                }
            }

            if (currSnapshot != null) {
                snapshots.add(currSnapshot);
            }
            prevSnapshot = currSnapshot;
        }

        if (!brokenSnapshots.isEmpty()) {
            log.warn("Migration for user {}, Found {} broken snapshots: {}", account.getLogin(),
                    brokenSnapshots.size(),
                    brokenSnapshots.stream()
                            .map(TrackingSchema::getTimestamp)
                            .map(LocalDateTime::toString)
                            .collect(Collectors.joining(","))
            );
        }

        snapshotRepository.saveAllAndFlush(snapshots);
        log.info("Successfully migrate {} snapshots", snapshots.size());
    }


    // TODO: Premium migration
    private void migrateDiffs(AccountDocument account, TrackTargetDTO target) {
        migrateDiffs(account.getDaily(), TrackDiffPeriod.DAY, target);
        migrateDiffs(account.getWeekly(), TrackDiffPeriod.WEEK, target);
        migrateDiffs(account.getMonthly(), TrackDiffPeriod.MONTH, target);
    }

    private void migrateDiffs(List<TrackingSchema> schemas, TrackDiffPeriod period, TrackTargetDTO target) {
        List<TrackDiffRecord> records = schemas.stream()
                .peek(this::fixTrackingSchema)
                .map(schema -> toDiff(schema, period, target))
                .collect(Collectors.toList());
        diffRepository.saveAllAndFlush(records);
        log.info("Successfully migrate {} diffs for {} period", records.size(), period);
    }

    private TrackDiffRecord toDiff(TrackingSchema diff, TrackDiffPeriod period, TrackTargetDTO target) {
        TrackDiffRecord res = new TrackDiffRecord();
        TrackTargetRecord targetRec = new TrackTargetRecord();
        targetRec.setId(target.getId());

        res.setTarget(targetRec);
        res.setTrackRecord(schemaMapper.toRecord(diff));
        res.setPeriod(period);

        DatePeriod datePeriod = period.getDatePeriod(diff.getTimestamp());
        res.setPeriodStart(datePeriod.getStart());
        res.setPeriodEnd(datePeriod.getEnd());
        res.setTrackStart(datePeriod.getStart());
        res.setTrackEnd(datePeriod.getEnd());
        return res;
    }

    private TrackSnapshotRecord toSnapshot(TrackingSchema snapshot, TrackTargetDTO target) {
        TrackSnapshotRecord res = new TrackSnapshotRecord();
        res.setTarget(new TrackTargetRecord(target.getId()));
        res.setTimestamp(snapshot.getTimestamp());
        res.setTrackRecord(schemaMapper.toRecord(snapshot));
        return res;
    }

    private boolean premiumMatches(TrackingSchema schema, TrackRecord record) {
        return schema.getHasPremium() == (record.getPremium() > 0);
    }

    private void fixTrackingSchema(TrackingSchema trackingSchema) {
        trackingSchema.getSupplies().removeIf(supplySchema -> supplySchema.getName() == null);
        trackingSchema.getSupplies().add(new TrackSupplySchema(TankiSupply.NUCLEAR.name(), 0));
        trackingSchema.setTimestamp(trackingSchema.getTimestamp()
                .minus(3, ChronoUnit.HOURS)
                .plusDays(1)
        );
    }

    private TrackTargetDTO createAccount(String login) {
        var res = trackTargetService.create(login, TrackTargetType.ACCOUNT);
        log.info("Create account {}", login);
        return res;
    }

}
