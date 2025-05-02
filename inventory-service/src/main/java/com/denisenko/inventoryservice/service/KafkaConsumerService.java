package com.denisenko.inventoryservice.service;

import com.denisenko.events.*;
import com.denisenko.inventoryservice.config.KafkaTopicsConfig;
import com.denisenko.inventoryservice.dto.DocumentDto;
import com.denisenko.inventoryservice.dto.IngredientDto;
import com.denisenko.inventoryservice.dto.InventoryMovementDto;
import com.denisenko.inventoryservice.model.DocumentType;
import com.denisenko.inventoryservice.model.MovementType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
    private final DocumentService documentService;
    private final InventoryService inventoryService;
    private final RecipeService recipeService;
    private final KafkaProducerService kafkaProducerService;
    private final KafkaTopicsConfig kafkaTopicsConfig;

    @KafkaListener(topics = "#{kafkaTopicsConfig.supplyReceivedTopic}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleSupplyReceivedEvent(SupplyReceivedEvent event) {
        log.info("Supply received with document number = {}", event.getDocumentNumber());
        List<InventoryMovementDto> inventoryMovements = new ArrayList<>();
        event.getItems().forEach(item -> inventoryMovements.add(InventoryMovementDto.builder()
                .ingredientId(item.getIngredientId())
                .quantity(item.getQuantity())
                .cost(item.getCost())
                .movementType(MovementType.RECEIVED.name())
                .movementDate(event.getAcceptedDate().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .build()));

        DocumentDto documentDto = DocumentDto.builder()
                .documentNumber(event.getDocumentNumber())
                .documentDate(event.getDocumentDate())
                .documentType(DocumentType.SUPPLY.name())
                .supplierName(event.getSupplierName())
                .inventoryMovements(inventoryMovements)
                .build();

        String idempotencyKey = UUID.randomUUID().toString();
        documentService.createDocument(documentDto, idempotencyKey);
    }

    @KafkaListener(topics = "#{kafkaTopicsConfig.orderClosedTopic}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleOrderClosedEvent(OrderClosedEvent event) {
        log.info("Closed order received");
        Map<Integer, BigDecimal> recipeCosts = recipeService.getRecipeCosts(event.getItems().stream().map(OrderClosedItem::getItemId).toList());
        List<InventoryMovementDto> inventoryMovements = new ArrayList<>();
        event.getItems().stream()
                .filter(item -> item.getStatus() == OrderItemStatus.ACTIVE)
                .forEach(item -> inventoryMovements.add(InventoryMovementDto.builder()
                        .ingredientId(item.getItemId())
                        .quantity(item.getQuantity())
                        .cost(recipeCosts.get(item.getItemId()))
                        .movementType(MovementType.DEDUCTED.name())
                        .movementDate(LocalDateTime.now())
                        .build()));

        DocumentDto documentDto = DocumentDto.builder()
                .documentNumber(event.getOfdNumber())
                .documentDate(event.getClosedAt().atZone(ZoneId.systemDefault()).toLocalDateTime().toLocalDate())
                .documentType(DocumentType.ORDER.name())
                .inventoryMovements(inventoryMovements)
                .build();

        String idempotencyKey = UUID.randomUUID().toString();
        documentService.createDocument(documentDto, idempotencyKey);
    }

    @KafkaListener(topics = "#{kafkaTopicsConfig.supplyOrderRequestTopic}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleInventoryRequestEvent(InventoryRequestEvent event) {
        log.info("Inventory request received with requestId = {}", event.getRequestId());
        Map<String, Double> inventory = inventoryService.getInventory(event.getSystemProductIds().stream().map(Integer::valueOf).toList()).stream()
                .collect(Collectors.toMap(ingredient -> String.valueOf(ingredient.getId()), IngredientDto::getQuantity));

        kafkaProducerService.sendSupplyOrderResponse(new InventoryResponseEvent(event.getRequestId(), inventory));
    }
}
