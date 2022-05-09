package me.fizzika.tankirating.service.tracking;

import me.fizzika.tankirating.dto.TrackTargetDTO;
import me.fizzika.tankirating.enums.track.TrackTargetType;

import java.util.List;
import java.util.Optional;

public interface TrackTargetService {

    List<TrackTargetDTO> getAllTargets();

    Optional<TrackTargetDTO> getByName(String name, TrackTargetType type);

    boolean existsByName(String name, TrackTargetType type);

    TrackTargetDTO create(String name, TrackTargetType type);
}
