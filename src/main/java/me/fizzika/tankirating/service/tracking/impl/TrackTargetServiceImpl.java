package me.fizzika.tankirating.service.tracking.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.dto.TrackTargetDTO;
import me.fizzika.tankirating.dto.filter.TrackTargetFilter;
import me.fizzika.tankirating.enums.ExceptionType;
import me.fizzika.tankirating.enums.track.TrackTargetType;
import me.fizzika.tankirating.exceptions.ExternalException;
import me.fizzika.tankirating.mapper.TrackTargetMapper;
import me.fizzika.tankirating.record.tracking.TrackTargetRecord;
import me.fizzika.tankirating.repository.tracking.TrackTargetRepository;
import me.fizzika.tankirating.service.tracking.TrackTargetService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class TrackTargetServiceImpl implements TrackTargetService {

    private final TrackTargetRepository repository;
    private final TrackTargetMapper mapper;

    @Override
    public List<TrackTargetDTO> getAllTargets() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TrackTargetDTO> getOptionalByName(String name, TrackTargetType type) {
        return repository.findByNameIgnoreCaseAndType(name, type)
                .map(mapper::toDto);
    }

    @Override
    public Page<TrackTargetDTO> findAll(TrackTargetFilter filter, Pageable pageable) {
        return repository.findAll(filter.getTargetType(), filter.getQuery(),  pageable)
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

}
