package me.fizzika.tankirating.service.tracking;

import me.fizzika.tankirating.dto.TrackTargetDTO;

import java.util.List;
import java.util.Optional;

public interface TrackTargetService {

    List<TrackTargetDTO> getAllTargets();

    Optional<TrackTargetDTO> getByName(String name);

    boolean existsByName(String name);

    TrackTargetDTO create(String name);

}
