package me.fizzika.tankirating.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.*;

@Configuration
public class AsyncConfig {

    @Value("${app.pool.api.max-threads}")
    private Integer maxApiPoolSize;
    @Value("${app.pool.migration.max-threads}")
    private Integer maxMigrationPoolSize;

    @Value("${app.pool.api.tll}")
    private Duration apiTtl;
    @Value("${app.pool.migration.ttl}")
    private Duration migrationTtl;

    @Bean("apiTaskExecutor")
    public Executor apiTaskExecutor() {
        var executor = new ThreadPoolExecutor(maxApiPoolSize, maxApiPoolSize, apiTtl.toSeconds(),
                TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    @Bean("migrationTaskExecutor")
    public Executor migrationTaskExecutor() {
        var executor = new ThreadPoolExecutor(maxMigrationPoolSize, maxMigrationPoolSize, migrationTtl.toSeconds(),
                TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }
}
