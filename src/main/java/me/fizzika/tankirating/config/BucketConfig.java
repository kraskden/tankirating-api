package me.fizzika.tankirating.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.config.properties.AlternativaApiProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BucketConfig {

    private final AlternativaApiProperties alternativaApiProperties;

    @Bean("alternativaApiBucket")
    public Bucket alternativaApiBucket() {
        Bandwidth rpmLimit = Bandwidth.builder()
                                        .capacity(alternativaApiProperties.getMaxRpm())
                                        .refillGreedy(alternativaApiProperties.getMaxRpm(), Duration.ofMinutes(1))
                                        .build();

        Bandwidth rpsLimit = Bandwidth.builder()
                                         .capacity(alternativaApiProperties.getMaxRps())
                                         .refillGreedy(alternativaApiProperties.getMaxRps(), Duration.ofSeconds(1))
                                         .build();

        return Bucket.builder()
                     .addLimit(rpmLimit)
                     .addLimit(rpsLimit)
                     .build();
    }

}