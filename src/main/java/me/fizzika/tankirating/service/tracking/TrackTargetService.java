package me.fizzika.tankirating.service.tracking;

import me.fizzika.tankirating.dto.TrackTargetDTO;
import me.fizzika.tankirating.dto.filter.TrackTargetFilter;
import me.fizzika.tankirating.enums.ExceptionType;
import me.fizzika.tankirating.enums.track.TrackTargetStatus;
import me.fizzika.tankirating.enums.track.TrackTargetType;
import me.fizzika.tankirating.exceptions.ExternalException;
import me.fizzika.tankirating.model.TrackGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TrackTargetService {

    List<TrackTargetDTO> getAll();

    List<TrackTargetDTO> getAll(TrackTargetType type);

    List<TrackGroup> getAllGroups();

    Map<TrackTargetType, List<TrackTargetDTO>> getAllTargetsMap();

    Optional<TrackTargetDTO> getOptionalByName(String name, TrackTargetType type);

    Optional<TrackTargetDTO> getOptionalById(Integer id, TrackTargetType type);

    default TrackTargetDTO getByName(String name, TrackTargetType type) {
        return getOptionalByName(name, type)
                .orElseThrow(() -> new ExternalException(ExceptionType.TRACK_TARGET_NOT_FOUND)
                        .arg("name", name)
                        .arg("type", type)
                );
    }

    List<TrackTargetDTO> findAll(TrackTargetFilter filter, Sort sort);

    Page<TrackTargetDTO> findAll(TrackTargetFilter filter, Pageable pageable);

    boolean existsByName(String name, TrackTargetType type);

    TrackTargetDTO create(String name, TrackTargetType type);

    TrackTargetDTO update(Integer id, TrackTargetDTO updated);

}
