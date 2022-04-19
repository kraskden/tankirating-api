package me.fizzika.tankirating.service.tracking.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.dto.alternativa.AlternativaTrackDTO;
import me.fizzika.tankirating.dto.alternativa.AlternativaTrackResponseDTO;
import me.fizzika.tankirating.service.tracking.AlternativaTrackingService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * Mock service for testing purposes
 */
@Service
@Slf4j
@Qualifier("MOCK_ALTERNATIVA_TRACKING_SERVICE")
@RequiredArgsConstructor
public class MockAlternativaTrackingService implements AlternativaTrackingService {

    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public AlternativaTrackDTO getTracking(String username) {
        var res = objectMapper.readValue(new File("/home/den/docs/fizzika.json"), AlternativaTrackResponseDTO.class);
        return res.getTrack();
    }

}
