package com.denisenko.orderservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCacheService {
    private final RedisTemplate<String, byte[]> redisTemplate;
    private final ObjectMapper objectMapper;

    public <T> T get(String key, Class<T> tClass) {
        byte[] bytes = redisTemplate.opsForValue().get(key);
        if (bytes != null) {
            try {
                return objectMapper.readValue(bytes, tClass);
            } catch (Exception e) {
                log.warn("Failed to deserialize data for key {}. Error: {}", key, e.getMessage(), e);
            }
        }
        return null;
    }

    public <T> T get(String key, TypeReference<T> typeReference) {
        byte[] bytes = redisTemplate.opsForValue().get(key);
        if (bytes != null) {
            try {
                return objectMapper.readValue(bytes, typeReference);
            } catch (Exception e) {
                log.warn("Failed to deserialize data for key {}. Error: {}", key, e.getMessage(), e);
            }
        }
        return null;
    }

    public <T> void put(String key, T object) {
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(object);
            redisTemplate.opsForValue().set(key, bytes, Duration.ofMinutes(10));
        } catch (Exception e) {
            log.warn("Failed to serialize data for key {}. Error: {}", key, e.getMessage(), e);
        }
    }
}
