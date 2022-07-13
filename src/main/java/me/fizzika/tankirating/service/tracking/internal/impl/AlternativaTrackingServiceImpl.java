package me.fizzika.tankirating.service.tracking.internal.impl;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.alternativa.track.AlternativaTrackDTO;
import me.fizzika.tankirating.dto.alternativa.track.AlternativaTrackResponseDTO;
import me.fizzika.tankirating.exceptions.ExternalException;
import me.fizzika.tankirating.service.tracking.internal.AlternativaTrackingService;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AlternativaTrackingServiceImpl implements AlternativaTrackingService {

    private static final String URL_TEMPLATE = "https://ratings.tankionline.com/api/eu/profile/?user={user}&lang=en";
    private static final String OK_RESPONSE = "OK";

    private final RestTemplate restTemplate;

    @Async
    @Override
    public CompletableFuture<AlternativaTrackDTO> getTracking(String username) {
        var response = restTemplate.getForObject(URL_TEMPLATE, AlternativaTrackResponseDTO.class, username);
        if (response != null && OK_RESPONSE.equals(response.getResponseType())) {
            return CompletableFuture.completedFuture(response.getTrack());
        } else {
            return CompletableFuture.failedFuture(new ExternalException("User {} is not found", HttpStatus.NOT_FOUND)
                    .arg("username", username));
        }
    }

}
