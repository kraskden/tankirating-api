package me.fizzika.tankirating.service.tracking.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.dto.TrackTargetDTO;
import me.fizzika.tankirating.repository.TrackTargetRepository;
import me.fizzika.tankirating.service.tracking.TrackTargetService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class TrackTargetServiceImpl implements TrackTargetService {

    private final TrackTargetRepository repository;

    @Override
    public List<TrackTargetDTO> getAllTargets() {
        return repository.findAll().stream()
                .map(r -> new TrackTargetDTO(r.getId(), r.getName()))
                .collect(Collectors.toList());
    }

}
