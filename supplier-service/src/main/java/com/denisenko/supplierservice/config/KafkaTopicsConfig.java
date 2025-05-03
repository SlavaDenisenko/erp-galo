package com.denisenko.supplierservice.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class KafkaTopicsConfig {

    @Value("${spring.kafka.topics.supply-request}")
    private String supplyRequestTopic;

    @Value("${spring.kafka.topics.supply-received}")
    private String supplyReceivedTopic;

    @Value("${spring.kafka.topics.supply-response}")
    private String supplyResponseTopic;
}
