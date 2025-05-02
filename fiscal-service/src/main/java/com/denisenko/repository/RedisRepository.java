package com.denisenko.repository;

import com.denisenko.exception.RedisStorageException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.mutiny.redis.client.Command;
import io.vertx.mutiny.redis.client.Redis;
import io.vertx.mutiny.redis.client.Request;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class RedisRepository {
    private static final Logger log = LoggerFactory.getLogger(RedisRepository.class);

    @Inject
    Redis redis;

    @Inject
    ObjectMapper objectMapper;

    public <T> void save(String key, T value) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redis.send(Request.cmd(Command.SET).arg(key).arg(json))
                    .subscribe().with(
                            success -> log.info("Successfully saved key: {}", key),
                            failure -> {
                                String errorMessage = "Failed to save key: " + key;
                                log.error(errorMessage, failure);
                                throw new RedisStorageException(errorMessage, failure);
                            }
                    );
        } catch (Exception e) {
            log.error("Serialization failed for key: {}", key, e);
            throw new RuntimeException("Failed to serialize object", e);
        }
    }

    public <T> void saveToList(String listKey, T value) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redis.send(Request.cmd(Command.RPUSH).arg(listKey).arg(json))
                    .subscribe().with(
                            success -> log.info("Successfully added to list: {}", listKey),
                            failure -> {
                                String errorMessage = "Failed to add to list: " + listKey;
                                log.error(errorMessage, failure);
                                throw new RedisStorageException(errorMessage, failure);
                            }
                    );
        } catch (Exception e) {
            log.error("Serialization failed for listKey: {}", listKey, e);
            throw new RuntimeException("Failed to serialize object", e);
        }
    }

    public <T> Optional<T> findByKey(String key, Class<T> clazz) {
        return redis.send(Request.cmd(Command.GET).arg(key))
                .map(response -> {
                    if (Objects.isNull(response)) {
                        log.warn("No value found for key: {}", key);
                        return Optional.<T>empty();
                    }
                    return Optional.ofNullable(deserializeValue(response.toString(), clazz, key));
                })
                .await().indefinitely();
    }

    public <T> List<T> findAllFromList(String listKey, Class<T> clazz) {
        return redis.send(Request.cmd(Command.LRANGE).arg(listKey).arg("0").arg("-1"))
                .map(response -> {
                    if (Objects.isNull(response)) {
                        log.warn("No values found for listKey {}", listKey);
                        return new ArrayList<T>();
                    }
                    List<T> values = new ArrayList<>();
                    response.forEach(value -> values.add(deserializeValue(value.toString(), clazz, listKey)));
                    return values;
                })
                .await().indefinitely();
    }

    public void delete(String key) {
        redis.send(Request.cmd(Command.DEL).arg(key))
                .subscribe().with(
                        success -> log.info("Successfully deleted key: {}", key),
                        failure -> {
                            String errorMessage = "Failed to delete key: " + key;
                            log.error(errorMessage, failure);
                            throw new RedisStorageException(errorMessage, failure);
                        }
                );
    }

    private <T> T deserializeValue(String json, Class<T> clazz, String key) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error("Failed to deserialize value for key: {}", key, e);
            throw new RuntimeException("Failed to deserialize object for key: " + key, e);
        }
    }
}
