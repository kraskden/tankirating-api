package me.fizzika.tankirating.config;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.config.properties.AlternativaApiProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {

    private final AlternativaApiProperties alternativaApiProperties;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .connectTimeout(alternativaApiProperties.getConnectTimeout())
                .readTimeout(alternativaApiProperties.getReadTimeout())
                .build();
    }
}