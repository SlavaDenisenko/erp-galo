package com.denisenko.supplyorderservice.service;

import com.denisenko.events.InventoryRequestEvent;
import com.denisenko.events.SupplyResponseEvent;
import com.denisenko.supplyorderservice.config.KafkaTopicsConfig;
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

    public void sendInventoryRequestEvent(InventoryRequestEvent event) {
        log.info("Preparing to send inventory request event: {}", event);
        kafkaTemplate.send(kafkaTopicsConfig.getSupplyOrderRequestTopic(), event);
    }

    public void sendSupplyResponseEvent(SupplyResponseEvent event) {
        log.info("Preparing to send supply response event: {}", event);
        kafkaTemplate.send(kafkaTopicsConfig.getSupplyResponseTopic(), event);
    }
}
