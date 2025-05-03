package com.denisenko.supplierservice.service;

import com.denisenko.events.SupplyReceivedEvent;
import com.denisenko.events.SupplyRequestEvent;
import com.denisenko.supplierservice.config.KafkaTopicsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicsConfig kafkaTopicsConfig;

    public void sendSupplyOrderRequest(SupplyRequestEvent event) {
        log.info("Preparing to send supply request event: {}", event);
        kafkaTemplate.send(kafkaTopicsConfig.getSupplyRequestTopic(), event);
    }

    public void sendSupplyReceivedEvent(SupplyReceivedEvent event) {
        log.info("Preparing to send supply received event: {}", event);
        kafkaTemplate.send(kafkaTopicsConfig.getSupplyReceivedTopic(), event);
    }
}
