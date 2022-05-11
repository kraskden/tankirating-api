package me.fizzika.tankirating.v1_migration.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.dto.TrackTargetDTO;
import me.fizzika.tankirating.enums.track.TankiSupply;
import me.fizzika.tankirating.enums.track.TrackDiffPeriod;
import me.fizzika.tankirating.enums.track.TrackTargetType;
import me.fizzika.tankirating.model.DatePeriod;
import me.fizzika.tankirating.record.tracking.TrackDiffRecord;
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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountMigrationService implements V1MigrationService {

    private final AccountMongoRepository mongoRepository;
    private final AccountMongoTemplateRepository mongoTemplateRepository;

    private final TrackTargetService trackTargetService;
    private final TrackingSchemaMapper schemaMapper;

    private final TrackSnapshotRepository snapshotRepository;
    private final TrackDiffRepository diffRepository;

    @Override
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

    private void migrateSnapshots(AccountDocument account, TrackTargetDTO target) {
        List<TrackSnapshotRecord> snapshots = account.getTracking().stream()
                .peek(this::fixTrackingSchema)
                .map(schema -> toSnapshot(schema, target))
                .collect(Collectors.toList());
        snapshotRepository.saveAllAndFlush(snapshots);
        log.info("Successfully migrate {} snapshots", snapshots.size());
    }


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
        res.setTimestamp(getCorrectTime(snapshot.getTimestamp()));
        res.setTrackRecord(schemaMapper.toRecord(snapshot));
        return res;
    }

    private LocalDateTime getCorrectTime(LocalDateTime schemaTimestamp) {
        return schemaTimestamp
                .minus(3, ChronoUnit.HOURS)
                .plus(1, ChronoUnit.DAYS);
    }

    private void fixTrackingSchema(TrackingSchema trackingSchema) {
        trackingSchema.getSupplies().removeIf(supplySchema -> supplySchema.getName() == null);
        trackingSchema.getSupplies().add(new TrackSupplySchema(TankiSupply.NUCLEAR.name(), 0));
    }

    private TrackTargetDTO createAccount(String login) {
        var res = trackTargetService.create(login, TrackTargetType.ACCOUNT);
        log.info("Create account {}", login);
        return res;
    }

}
