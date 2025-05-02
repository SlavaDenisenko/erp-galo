package com.denisenko.cache;

import com.denisenko.exception.RedisStorageException;
import com.denisenko.repository.RedisRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class LocalCache {
    private static final Logger log = LoggerFactory.getLogger(LocalCache.class);

    private final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Inject
    RedisRepository redisRepository;

    public LocalCache() {
        executorService.scheduleAtFixedRate(this::syncWithRedis, 1, 5, TimeUnit.MINUTES);
    }

    public void save(String key, Object value) {
        cache.put(key, value);
        log.warn("Object {} saved to local cache as Redis is unavailable", value.getClass());
    }

    public <T> void addToList(String key, T value) {
        cache.compute(key, (k, v) -> {
            if (Objects.isNull(v)) {
                List<T> newList = new ArrayList<>();
                newList.add(value);
                return newList;
            }
            if (v instanceof List) {
                @SuppressWarnings("unchecked")
                List<T> list = (List<T>) v;
                list.add(value);
                return list;
            }
            throw new IllegalStateException("Value under key " + key + " is not a list");
        });
    }

    public void remove(String key) {
        cache.remove(key);
    }

    private void syncWithRedis() {
        log.info("Starting synchronization of local cache with Redis");
        for (String key : cache.keySet()) {
            Object value = cache.get(key);
            try {
                if (value instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Object> list = (List<Object>) value;
                    list.forEach(item -> redisRepository.saveToList(key, item));
                } else {
                    redisRepository.save(key, value);
                }
                cache.remove(key);
                log.info("Successfully synchronized object {} with Redis", key);
            } catch (RedisStorageException e) {
                log.error("Failed to synchronize object {} with Redis", key, e);
            }
        }
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
