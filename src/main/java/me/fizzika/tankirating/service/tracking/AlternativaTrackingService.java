package me.fizzika.tankirating.service.tracking;

import me.fizzika.tankirating.dto.alternativa.AlternativaTrackDTO;

public interface AlternativaTrackingService {
    
    AlternativaTrackDTO getTracking(String username);
    
}
