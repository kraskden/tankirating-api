package me.fizzika.tankirating.service.tracking.internal;

import java.util.Set;
import me.fizzika.tankirating.dto.tracking.TrackEntityDTO;
import me.fizzika.tankirating.enums.track.TankiEntityType;

/**
 *
 * @implSpec {@link TrackEntityService#get(Short)} should work without database access.
 *
 * {@link TrackEntityService#getId(String, TankiEntityType)} should work without database access if entity with this
 * name and type is exists in the database, otherwise the method should create it and returns id
 */
public interface TrackEntityService {

    TrackEntityDTO get(Short id);

    Short getId(String name, TankiEntityType type);

    Set<String> getNames(TankiEntityType type);

}
