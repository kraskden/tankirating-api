package me.fizzika.tankirating.service.tracking.internal.impl;

import static io.github.bucket4j.BlockingStrategy.PARKING;

import io.github.bucket4j.BlockingStrategy;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.dto.alternativa.track.AlternativaTrackDTO;
import me.fizzika.tankirating.dto.alternativa.track.AlternativaTrackResponseDTO;
import me.fizzika.tankirating.exceptions.alternativa.AlternativaException;
import me.fizzika.tankirating.exceptions.alternativa.AlternativaServerUnavailableException;
import me.fizzika.tankirating.exceptions.alternativa.AlternativaTooManyRequestsException;
import me.fizzika.tankirating.exceptions.alternativa.AlternativaUserNotFoundException;
import me.fizzika.tankirating.service.tracking.internal.AlternativaTrackingService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlternativaTrackingServiceImpl implements AlternativaTrackingService {

    private static final String RATING_URL_TEMPLATE = "https://ratings.tankionline.com/api/eu/profile/?user={user}&lang=en";

    private static final String OK_RESPONSE = "OK";

    private final RestTemplate restTemplate;
    private final Bucket alternativaApiBucket;

    @Async("apiTaskExecutor")
    @Override
    public CompletableFuture<AlternativaTrackDTO> getTracking(String username) {
        try {
            alternativaApiBucket.asBlocking().consume(1, PARKING);

            var response = restTemplate.getForObject(RATING_URL_TEMPLATE, AlternativaTrackResponseDTO.class, username);
            if (response != null && OK_RESPONSE.equals(response.getResponseType())) {
                return CompletableFuture.completedFuture(response.getTrack());
            } else {
                return CompletableFuture.failedFuture(new AlternativaUserNotFoundException(
                        String.format("User %s is not found", username)));
            }
        } catch (RestClientException e) {
            if (e instanceof HttpClientErrorException.TooManyRequests) {
                throw new AlternativaTooManyRequestsException(e);
            } else {
                throw new AlternativaServerUnavailableException(e);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Async("apiTaskExecutor")
    public CompletableFuture<Void> healthCheck() {
        try {
            String randomUsername = UUID.randomUUID().toString();
            restTemplate.getForObject(RATING_URL_TEMPLATE, AlternativaTrackResponseDTO.class, randomUsername);
            return CompletableFuture.completedFuture(null);
        } catch (Exception ex) {
            return CompletableFuture.failedFuture(new AlternativaException(ex));
        }
    }

}