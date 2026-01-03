package me.fizzika.tankirating.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Valid
@Component
@ConfigurationProperties(prefix = "app.alternativa-api")
public class AlternativaApiProperties {

    @NotNull
    private Duration readTimeout;
    @NotNull
    private Duration connectTimeout;

    @NotNull
    private Long maxRps;
    @NotNull
    private Long maxRpm;
}