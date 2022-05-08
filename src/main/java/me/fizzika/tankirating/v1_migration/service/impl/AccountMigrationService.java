package me.fizzika.tankirating.v1_migration.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.dto.TrackTargetDTO;
import me.fizzika.tankirating.enums.track.TankiSupply;
import me.fizzika.tankirating.record.tracking.TrackSnapshotRecord;
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

        TrackTargetDTO target = trackTargetService.getByName(login)
                .orElseGet(() -> createAccount(login));

        migrateSnapshots(account, target);

    }

    private void migrateSnapshots(AccountDocument account, TrackTargetDTO target) {
        List<TrackSnapshotRecord> snapshots = account.getTracking().stream()
                .peek(this::fixTrackingSchema)
                .map(schema -> toSnapshot(schema, target))
                .collect(Collectors.toList());
        snapshotRepository.saveAllAndFlush(snapshots);
        log.debug("Successfully migrate {} snapshots", snapshots.size());
    }

    private TrackSnapshotRecord toSnapshot(TrackingSchema snapshot, TrackTargetDTO target) {
        TrackSnapshotRecord res = new TrackSnapshotRecord();
        res.setTargetId(target.getId());
        res.setTimestamp(snapshot.getTimestamp());
        res.setTrackRecord(schemaMapper.toRecord(snapshot));
        return res;
    }

    private void fixTrackingSchema(TrackingSchema trackingSchema) {
        trackingSchema.getSupplies().removeIf(supplySchema -> supplySchema.getName() == null);
        trackingSchema.getSupplies().add(new TrackSupplySchema(TankiSupply.NUCLEAR.name(), 0));
    }

    private TrackTargetDTO createAccount(String login) {
        var res = trackTargetService.create(login);
        log.debug("Create account {}", login);
        return res;
    }

}
