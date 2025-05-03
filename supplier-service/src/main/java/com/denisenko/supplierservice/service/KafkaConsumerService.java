package com.denisenko.supplierservice.service;

import com.denisenko.events.SupplyResponseEvent;
import com.denisenko.supplierservice.config.KafkaTopicsConfig;
import com.denisenko.supplierservice.dto.OrderItemDto;
import com.denisenko.supplierservice.dto.SupplierOrderDto;
import com.denisenko.supplierservice.model.RequestLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.denisenko.supplierservice.model.RequestStatus.RECEIVED;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
    private final OrderService orderService;
    private final RequestService requestService;
    private final KafkaTopicsConfig kafkaTopicsConfig;

    @KafkaListener(topics = "#{kafkaTopicsConfig.supplyResponseTopic}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleSupplyResponseEvent(SupplyResponseEvent event) {
        log.info("Supply response received with requestId = {}", event.getRequestId());
        Optional<RequestLog> request = requestService.getRequest(event.getRequestId());
        if (request.isEmpty()) {
            log.error("Error: request with ID = {} wasn't found!", event.getRequestId());
            return;
        }

        RequestLog requestLog = request.get();
        requestLog.setStatus(RECEIVED);
        requestService.saveAll(List.of(requestLog));

        List<OrderItemDto> items = event.getOrderedQuantities().entrySet().stream()
                .map(entry -> OrderItemDto.builder().productId(entry.getKey()).quantity(entry.getValue()).build())
                .toList();

        SupplierOrderDto supplierOrderDto = SupplierOrderDto.builder()
                .supplierId(requestLog.getSupplier().getId())
                .items(items)
                .build();

        String idempotencyKey = UUID.randomUUID().toString();
        orderService.createOrder(supplierOrderDto, idempotencyKey);
    }
}
