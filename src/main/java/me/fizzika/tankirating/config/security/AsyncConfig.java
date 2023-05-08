package me.fizzika.tankirating.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Value("${app.pool.api.max-threads}")
    private Integer maxApiPoolSize;

    @Value("${app.pool.api.tll}")
    private Duration apiTtl;

    @Bean("apiTaskExecutor")
    public Executor apiTaskExecutor() {
        return new ThreadPoolExecutor(0, maxApiPoolSize, apiTtl.toSeconds(),
                TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    }
}
