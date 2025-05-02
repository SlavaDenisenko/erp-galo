package com.denisenko.orderservice.integration;

import com.redis.testcontainers.RedisContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class ContainerConfig {
    static String REDIS_IMAGE = "redis:5.0.3-alpine";
    static int REDIS_PORT = 6379;

    @Bean
    RedisContainer redisContainer(DynamicPropertyRegistry registry) {
        var redisContainer = new RedisContainer(DockerImageName.parse(REDIS_IMAGE))
                .withExposedPorts(REDIS_PORT);
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(REDIS_PORT).toString());
        return redisContainer;
    }
}
