package me.fizzika.tankirating.service.online;

import me.fizzika.tankirating.dto.alternativa.online.AlternativaOnlineDTO;

import java.util.Optional;

public interface AlternativaOnlineService {

    Optional<AlternativaOnlineDTO> getOnlineData();

}
