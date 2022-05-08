package me.fizzika.tankirating.service.tracking.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.dto.TrackTargetDTO;
import me.fizzika.tankirating.enums.ExceptionType;
import me.fizzika.tankirating.exceptions.ExternalException;
import me.fizzika.tankirating.mapper.TrackTargetMapper;
import me.fizzika.tankirating.record.tracking.TrackTargetRecord;
import me.fizzika.tankirating.repository.TrackTargetRepository;
import me.fizzika.tankirating.service.tracking.TrackTargetService;
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
    public Optional<TrackTargetDTO> getByName(String name) {
        return repository.findByNameIgnoreCase(name)
                .map(mapper::toDto);
    }

    @Override
    public boolean existsByName(String name) {
        return repository.existsByNameIgnoreCase(name);
    }

    @Override
    public TrackTargetDTO create(String name) {
        if (existsByName(name)) {
            throw new ExternalException(ExceptionType.TRACK_TARGET_ALREADY_EXISTS)
                    .arg("name", name);
        }
        TrackTargetRecord rec = new TrackTargetRecord(name);
        return mapper.toDto(repository.save(rec));
    }


}
