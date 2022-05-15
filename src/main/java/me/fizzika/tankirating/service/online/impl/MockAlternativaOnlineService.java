package me.fizzika.tankirating.service.online.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.fizzika.tankirating.dto.alternativa.online.AlternativaOnlineDTO;
import me.fizzika.tankirating.service.online.AlternativaOnlineService;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
public class MockAlternativaOnlineService implements AlternativaOnlineService {

    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public AlternativaOnlineDTO getOnlineData() {
        return objectMapper.readValue(new File("/home/den/docs/status.js"), AlternativaOnlineDTO.class);
    }

}
