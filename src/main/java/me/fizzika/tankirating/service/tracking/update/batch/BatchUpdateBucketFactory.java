package me.fizzika.tankirating.service.tracking.update.batch;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.config.properties.BatchUpdateProperties;
import me.fizzika.tankirating.config.properties.BatchUpdateProperties.BatchUpdateEntryConfig;
import me.fizzika.tankirating.enums.track.TrackTargetStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BatchUpdateBucketFactory {

    private final BatchUpdateProperties batchUpdateProperties;

    @PostConstruct
    public void init() {
        System.out.printf("test");
    }

    /**
     * Limit RPM for batch update accounts based on status and accounts size
     */
    public Optional<Bucket> createBucketForBatchUpdate(TrackTargetStatus status, int accountsSize) {
        return Optional.ofNullable(batchUpdateProperties.getConfig().get(status))
                .map(BatchUpdateEntryConfig::duration)
                .map(duration -> buildBucket(duration, accountsSize));
    }

    private Bucket buildBucket(Duration updateDuration, int accountsSize) {
        long rpm = Math.max((accountsSize / updateDuration.toMinutes()) + 1, batchUpdateProperties.getMinRpm());

        Bandwidth bandwidth = Bandwidth.builder()
                                       .capacity(rpm)
                                       .refillGreedy(rpm, Duration.ofMinutes(1))
                                       .build();
        return Bucket.builder()
                     .addLimit(bandwidth)
                     .build();
    }
}