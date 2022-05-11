package me.fizzika.tankirating.service.tracking.internal;

import me.fizzika.tankirating.dto.alternativa.AlternativaTrackDTO;

import java.util.concurrent.CompletableFuture;

public interface AlternativaTrackingService {
    
    CompletableFuture<AlternativaTrackDTO> getTracking(String username);
    
}
