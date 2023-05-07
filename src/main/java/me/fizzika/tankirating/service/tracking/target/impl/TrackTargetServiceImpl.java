package me.fizzika.tankirating.service.tracking.target.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.dto.target.TrackTargetDTO;
import me.fizzika.tankirating.dto.filter.TrackTargetFilter;
import me.fizzika.tankirating.enums.ExceptionType;
import me.fizzika.tankirating.enums.track.GroupMeta;
import me.fizzika.tankirating.enums.track.TrackTargetStatus;
import me.fizzika.tankirating.enums.track.TrackTargetType;
import me.fizzika.tankirating.exceptions.ExternalException;
import me.fizzika.tankirating.mapper.TrackTargetMapper;
import me.fizzika.tankirating.model.TrackGroup;
import me.fizzika.tankirating.record.tracking.TrackTargetRecord;
import me.fizzika.tankirating.repository.tracking.TrackTargetRepository;
import me.fizzika.tankirating.service.tracking.target.TrackTargetService;
import me.fizzika.tankirating.v1_migration.repository.specification_builder.impl.TrackTargetSpecificationBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class TrackTargetServiceImpl implements TrackTargetService {

    private final TrackTargetRepository repository;
    private final TrackTargetMapper mapper;
    private final TrackTargetSpecificationBuilder specificationBuilder;

    @Override
    public List<TrackTargetDTO> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TrackTargetDTO> getAll(TrackTargetType type) {
        return repository.findAllByType(type).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TrackGroup> getAllGroups() {
        return getAll(TrackTargetType.GROUP).stream()
                .map(this::toTrackGroup)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    @Override
    public Map<TrackTargetType, List<TrackTargetDTO>> getAllTargetsMap() {
        Map<TrackTargetType, List<TrackTargetDTO>> res = Arrays.stream(TrackTargetType.values())
                .collect(Collectors.toMap(Function.identity(), (ignored) -> new ArrayList<>(), (fst, snd) -> fst,
                        () -> new EnumMap<>(TrackTargetType.class)));
        getAll().forEach(t -> res.get(t.getType()).add(t));
        return res;
    }

    @Override
    public Optional<TrackTargetDTO> getOptionalByName(String name, TrackTargetType type) {
        return repository.findByNameIgnoreCaseAndType(name, type)
                .map(mapper::toDto);
    }

    @Override
    public Optional<TrackTargetDTO> getOptionalById(Integer id, TrackTargetType type) {
        return repository.findByIdAndType(id, type)
                .map(mapper::toDto);
    }

    @Override
    public List<TrackTargetDTO> findAll(TrackTargetFilter filter, Sort sort) {
        return repository.findAll(specificationBuilder.filterToSpec(filter), sort).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<TrackTargetDTO> findAll(TrackTargetFilter filter, Pageable pageable) {
        return repository.findAll(specificationBuilder.filterToSpec(filter), pageable)
                .map(mapper::toDto);
    }


    @Override
    public boolean existsByName(String name, TrackTargetType type) {
        return repository.existsByNameIgnoreCaseAndType(name, type);
    }

    @Override
    public TrackTargetDTO create(String name, TrackTargetType type) {
        if (existsByName(name, type)) {
            throw new ExternalException(ExceptionType.TRACK_TARGET_ALREADY_EXISTS)
                    .arg("name", name);
        }
        TrackTargetRecord rec = new TrackTargetRecord(name, type);
        rec.setStatus(TrackTargetStatus.ACTIVE);
        return mapper.toDto(repository.save(rec));
    }

    @Override
    public TrackTargetDTO update(Integer id, TrackTargetDTO updated) {
        TrackTargetRecord rec = repository.findById(id)
                .orElseThrow(() -> new ExternalException(ExceptionType.TRACK_TARGET_NOT_FOUND)
                        .arg("id", id));
        mapper.update(updated, rec);
        return mapper.toDto(repository.save(rec));
    }

    private Optional<TrackGroup> toTrackGroup(TrackTargetDTO target) {
        var res = GroupMeta.fromName(target.getName())
                .map(meta -> new TrackGroup(target.getId(), meta));
        if (res.isEmpty()) {
            log.warn("Cannot find meta for group {}", target.getName());
        }
        return res;
    }

}
