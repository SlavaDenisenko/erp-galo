package com.denisenko.supplyorderservice.service;

import com.denisenko.events.InventoryResponseEvent;
import com.denisenko.events.SupplyRequestEvent;
import com.denisenko.supplyorderservice.config.KafkaTopicsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
    private final KafkaTopicsConfig kafkaTopicsConfig;
    private final SupplyOrderService supplyOrderService;

    @KafkaListener(topics = "#{kafkaTopicsConfig.supplyRequestTopic}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleSupplyRequestEvent(SupplyRequestEvent event) {
        log.info("Supply request received with requestId = {}", event.getRequestId());
        supplyOrderService.handleSupplyRequest(event);
    }

    @KafkaListener(topics = "#{kafkaTopicsConfig.supplyOrderResponseTopic}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleInventoryResponseEvent(InventoryResponseEvent event) {
        log.info("Inventory response received with requestId = {}", event.getRequestId());
        supplyOrderService.handleInventoryResponse(event);
    }
}
