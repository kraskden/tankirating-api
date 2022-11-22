package me.fizzika.tankirating.service.tracking.internal;

import me.fizzika.tankirating.dto.alternativa.track.AlternativaTrackDTO;

import java.util.concurrent.CompletableFuture;

public interface AlternativaTrackingService {
    
    CompletableFuture<AlternativaTrackDTO> getTracking(String username);

    CompletableFuture<Void> healthCheck();
    
}
