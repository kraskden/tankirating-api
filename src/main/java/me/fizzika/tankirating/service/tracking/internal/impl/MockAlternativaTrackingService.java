package me.fizzika.tankirating.service.tracking.internal.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.dto.alternativa.AlternativaTrackDTO;
import me.fizzika.tankirating.dto.alternativa.AlternativaTrackResponseDTO;
import me.fizzika.tankirating.mapper.AlternativaTrackingMapper;
import me.fizzika.tankirating.service.tracking.internal.AlternativaTrackingService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.concurrent.CompletableFuture;

/**
 * Mock service for testing purposes
 */
@Service
@Slf4j
@Qualifier("MOCK_ALTERNATIVA_TRACKING_SERVICE")
@RequiredArgsConstructor
public class MockAlternativaTrackingService implements AlternativaTrackingService {

    private final ObjectMapper objectMapper;
    private final AlternativaTrackingMapper alternativaTrackingMapper;

    @SneakyThrows
    @Override
    @Async
    public CompletableFuture<AlternativaTrackDTO> getTracking(String username) {
        Thread.sleep(1000);
        var res = objectMapper.readValue(new File("/home/den/docs/fizzika.json"), AlternativaTrackResponseDTO.class);
        if ("OK".equals(res.getResponseType())) {
            return CompletableFuture.completedFuture(res.getTrack());
        } else {
            return CompletableFuture.failedFuture(new RuntimeException("Wrong tracking..."));
        }
    }

}
