package com.denisenko.orderservice.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class KafkaTopicsConfig {

    @Value("${spring.kafka.topics.recipe-updated}")
    private String recipeUpdatedTopic;

    @Value("${spring.kafka.topics.recipe-deleted}")
    private String recipeDeletedTopic;

    @Value("${spring.kafka.topics.category-updated}")
    private String categoryUpdatedTopic;

    @Value("${spring.kafka.topics.category-deleted}")
    private String categoryDeletedTopic;

    @Value("${spring.kafka.topics.order-closed}")
    private String orderClosedTopic;
}
