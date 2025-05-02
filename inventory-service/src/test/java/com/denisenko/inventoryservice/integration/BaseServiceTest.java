package com.denisenko.inventoryservice.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles("test")
@SpringBootTest
@Import(ContainerConfig.class)
@TestPropertySource(properties = {
        "server.port=0",
        "spring.jpa.hibernate.ddl-auto=create",
        "spring.kafka.topics.recipe-updated=recipe-updated",
        "spring.kafka.topics.recipe-deleted=recipe-deleted",
        "spring.kafka.topics.category-updated=category-updated",
        "spring.kafka.topics.category-deleted=category-deleted",
        "spring.kafka.topics.supply-order-request=supply-order-request",
        "spring.kafka.topics.supply-order-response=supply-order-response",
        "spring.kafka.topics.supply-received=supply-received",
        "spring.kafka.topics.order-closed=order-closed",
        "spring.kafka.consumer.group-id=inventory-service-group"
})
public class BaseServiceTest {
}
