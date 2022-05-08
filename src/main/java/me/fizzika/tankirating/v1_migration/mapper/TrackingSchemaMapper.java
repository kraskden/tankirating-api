package me.fizzika.tankirating.v1_migration.mapper;

import me.fizzika.tankirating.enums.track.TankiSupply;
import me.fizzika.tankirating.enums.track.TrackActivityType;
import me.fizzika.tankirating.record.tracking.TrackActivityRecord;
import me.fizzika.tankirating.record.tracking.TrackRecord;
import me.fizzika.tankirating.record.tracking.TrackSupplyRecord;
import me.fizzika.tankirating.v1_migration.record.tracking.TrackSupplySchema;
import me.fizzika.tankirating.v1_migration.record.tracking.TrackingActivitySchema;
import me.fizzika.tankirating.v1_migration.record.tracking.TrackingSchema;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Map;
import java.util.Optional;

@Mapper(componentModel = "spring")
public abstract class TrackingSchemaMapper {

    public TrackRecord toRecord(TrackingSchema schema) {
        TrackRecord res = toRecordInternal(schema);
        res.getActivities().forEach(a -> a.setTrack(res));
        res.getSupplies().forEach(s -> s.setTrack(res));
        return res;
    }

    @Mapping(target = "premium", source = "hasPremium")
    public abstract TrackRecord toRecordInternal(TrackingSchema schema);

    @Mapping(target = "type", source = "role")
    protected abstract TrackActivityRecord toActivityRecord(TrackingActivitySchema schema);

    @Mapping(target = "usages", source = "count")
    @Mapping(target = "name", source = "name", qualifiedByName = "toSupplyName")
    protected abstract TrackSupplyRecord toSupplyRecord(TrackSupplySchema schema);

    protected int map(Boolean b) {
        return b == null || !b ? 0 : 1;
    }

    private final Map<String, TankiSupply> SCHEMA_SUPPLIES_MAP = Map.of(
            "Gold", TankiSupply.GOLDBOX
    );

    @Named("toSupplyName")
    protected String toSupplyName(String schemaSupplyName) {
        return Optional.ofNullable(SCHEMA_SUPPLIES_MAP.get(schemaSupplyName))
                .map(Enum::name)
                .orElseGet(() -> TankiSupply.valueOf(schemaSupplyName.toUpperCase()).name());
    }

    protected TrackActivityType map(String role) {
        return TrackActivityType.valueOf(role.toUpperCase());
    }

}
