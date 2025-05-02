package com.denisenko.reportservice.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@Import(ContainerConfig.class)
@TestPropertySource(properties = {
        "server.port=0",
        "spring.kafka.bootstrap-servers=my-cluster:9092",
        "spring.kafka.topics.shift-opened=shift-opened",
        "spring.kafka.topics.shift-closed=shift-closed",
        "spring.kafka.topics.order-closed=order-closed",
        "spring.kafka.consumer.group-id=report-service-group",
        "spring.data.redis.host=localhost",
        "spring.data.redis.port=6379",
        "spring.kafka.consumer.properties.apicurio.registry.url=http://localhost:8080",
        "spring.jpa.hibernate.ddl-auto=create"
})
public class BaseServiceTest {
}
