package me.fizzika.tankirating.v1_migration.mapper;

import lombok.Setter;
import me.fizzika.tankirating.enums.track.TankiEntityType;
import me.fizzika.tankirating.enums.track.TankiSupply;
import me.fizzika.tankirating.mapper.TrackRecordMapper;
import me.fizzika.tankirating.model.track_data.TrackFullData;
import me.fizzika.tankirating.record.tracking.TrackActivityRecord;
import me.fizzika.tankirating.record.tracking.TrackRecord;
import me.fizzika.tankirating.record.tracking.TrackUsageRecord;
import me.fizzika.tankirating.service.tracking.internal.TrackEntityService;
import me.fizzika.tankirating.v1_migration.record.tracking.TrackSupplySchema;
import me.fizzika.tankirating.v1_migration.record.tracking.TrackingActivitySchema;
import me.fizzika.tankirating.v1_migration.record.tracking.TrackingSchema;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Optional;

@Mapper(componentModel = "spring")
public abstract class TrackingSchemaMapper {

    @Setter(onMethod_ = {@Autowired})
    private TrackEntityService entityService;

    @Setter(onMethod_ = {@Autowired})
    private TrackRecordMapper recordMapper;

    public TrackFullData toDataModel(TrackingSchema schema) {
        TrackRecord record = toRecordInternal(schema);
        return recordMapper.toModel(record);
    }

    public TrackRecord toRecord(TrackingSchema schema) {
        if (schema == null || schema.getTime() == null || schema.getTime().equals(0)) {
            return null;
        }

        TrackRecord res = toRecordInternal(schema);
        res.getActivities().forEach(a -> a.setTrack(res));
        res.getSupplies().forEach(s -> s.setTrack(res));
        return res;
    }

    @Mapping(target = "premium", source = "hasPremium")
    @Mapping(target = "gold", source = "golds")
    public abstract TrackRecord toRecordInternal(TrackingSchema schema);

    @Mapping(target = "entityId", source = ".")
    protected abstract TrackActivityRecord toActivityRecord(TrackingActivitySchema schema);

    @Mapping(target = "usages", source = "count")
    @Mapping(target = "entityId", source = ".")
    protected abstract TrackUsageRecord toSupplyRecord(TrackSupplySchema schema);

    protected int map(Boolean b) {
        return b == null || !b ? 0 : 1;
    }

    private final Map<String, TankiSupply> SCHEMA_SUPPLIES_MAP = Map.of(
            "Gold", TankiSupply.GOLDBOX
    );

    protected Short toEntityId(TrackSupplySchema schema) {
        String name = Optional.ofNullable(SCHEMA_SUPPLIES_MAP.get(schema.getName()))
                .map(Enum::name)
                .orElseGet(() -> TankiSupply.valueOf(schema.getName().toUpperCase()).name());
        return entityService.getId(name, TankiEntityType.SUPPLY);
    }

    protected Short toEntityId(TrackingActivitySchema schema) {
        var entityType =  TankiEntityType.valueOf(schema.getRole().toUpperCase());
        return entityService.getId(schema.getName(), entityType);
    }

}
