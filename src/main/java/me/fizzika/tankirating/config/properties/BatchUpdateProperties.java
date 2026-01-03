package me.fizzika.tankirating.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import me.fizzika.tankirating.enums.track.TrackTargetStatus;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@Valid
@ConfigurationProperties(prefix = "app.batch-update")
public class BatchUpdateProperties {

    @NotNull
    private Long minRpm;

    private Map<TrackTargetStatus, BatchUpdateEntryConfig> config = new HashMap<>();

    @Valid
    public record BatchUpdateEntryConfig(Duration duration) {}
}