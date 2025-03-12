package me.fizzika.tankirating.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class Cache<K, V> {

    private final ConcurrentHashMap<K, CacheValue<V>> cache = new ConcurrentHashMap<>();
    private final Duration expiration;

    record CacheValue<V>(V data, LocalDateTime createdAt) {

    }
    public Cache(Duration expiration) {
        this.expiration = expiration;
    }

    public void clearAll() {
        cache.clear();
    }

    public V getOrCompute(K key, Function<? super K, ? extends V> computeFn) {
        LocalDateTime now = LocalDateTime.now();
        return cache.compute(key, (cacheKey, cacheVal) -> {
            if (cacheVal == null || cacheVal.createdAt.plus(expiration).isBefore(now)) {
                return new CacheValue<>(computeFn.apply(key), now);
            }
            return cacheVal;
        }).data;
    }
}