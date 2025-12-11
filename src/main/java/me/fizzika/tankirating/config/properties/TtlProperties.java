package me.fizzika.tankirating.config.properties;

import jakarta.annotation.PostConstruct;
import java.time.Period;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import me.fizzika.tankirating.enums.SnapshotPeriod;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ToString
@Slf4j
@Component
@ConfigurationProperties(prefix = "app.tracking.ttl")
public class TtlProperties {

    private Map<SnapshotPeriod, Period> snapshot;

    @PostConstruct
    public void log() {
        log.info("Use ttl config: {}", this);
    }
}