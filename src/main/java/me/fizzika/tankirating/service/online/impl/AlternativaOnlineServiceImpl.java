package me.fizzika.tankirating.service.online.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.dto.alternativa.online.AlternativaOnlineDTO;
import me.fizzika.tankirating.service.online.AlternativaOnlineService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlternativaOnlineServiceImpl implements AlternativaOnlineService {

    private static final String URL = "https://tankionline.com/s/status.js/";

    private final RestTemplate restTemplate;

    @Override
    public Optional<AlternativaOnlineDTO> getOnlineData() {
        try {
            return Optional.ofNullable(restTemplate.getForObject(URL, AlternativaOnlineDTO.class));
        } catch (RestClientException exception) {
            log.error("Online fetch fail", exception);
            return Optional.empty();
        }
    }
}
