package com.denisenko.orderservice.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@Import(ContainerConfig.class)
@TestPropertySource(properties = {
        "server.port=0",
        "spring.kafka.bootstrap-servers=my-cluster:9092",
        "spring.kafka.topics.recipe-updated=recipe-updated",
        "spring.kafka.topics.recipe-deleted=recipe-deleted",
        "spring.kafka.topics.category-updated=category-updated",
        "spring.kafka.topics.category-deleted=category-deleted",
        "spring.kafka.topics.order-closed=order-closed",
        "spring.kafka.consumer.group-id=order-service-group",
        "print-service.url=http://localhost:8080",
        "fiscal-service.url=http://localhost:8080",
        "inventory-service.url=http://localhost:8080",
        "feign.retry.maxAttempts=5",
        "feign.retry.delay=5000"
})
public class BaseServiceTest {
}
