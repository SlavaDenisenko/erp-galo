package com.denisenko.inventoryservice.service;

import com.denisenko.events.*;
import com.denisenko.inventoryservice.config.KafkaTopicsConfig;
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

    public void sendRecipeUpdatedEvent(RecipeUpdatedEvent event) {
        log.info("Preparing to send recipe updated event: {}", event);
        kafkaTemplate.send(kafkaTopicsConfig.getRecipeUpdatedTopic(), event);
    }

    public void sendRecipeDeletedEvent(RecipeDeletedEvent event) {
        log.info("Preparing to send recipe deleted event: {}", event);
        kafkaTemplate.send(kafkaTopicsConfig.getRecipeDeletedTopic(), event);
    }

    public void sendCategoryUpdatedEvent(CategoryUpdatedEvent event) {
        log.info("Preparing to send category updated event: {}", event);
        kafkaTemplate.send(kafkaTopicsConfig.getCategoryUpdatedTopic(), event);
    }

    public void sendCategoryDeletedEvent(CategoryDeletedEvent event) {
        log.info("Preparing to send category deleted event: {}", event);
        kafkaTemplate.send(kafkaTopicsConfig.getCategoryDeletedTopic(), event);
    }

    public void sendSupplyOrderResponse(InventoryResponseEvent event) {
        log.info("Preparing to send inventory response event: {}", event);
        kafkaTemplate.send(kafkaTopicsConfig.getSupplyOrderResponseTopic(), event);
    }
}
