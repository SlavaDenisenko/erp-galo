package com.denisenko.reportservice.service;

import com.denisenko.events.OrderClosedEvent;
import com.denisenko.events.ShiftClosedEvent;
import com.denisenko.events.ShiftOpenedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
    private final ShiftService shiftService;
    private final SaleService saleService;

    @KafkaListener(topics = "${spring.kafka.topics.shift-opened}", groupId = "${spring.kafka.consumer.group-id")
    public void handleShiftOpenedEvent(ShiftOpenedEvent event) {
        log.info("Received shift opened event: {}", event);
        shiftService.saveOpenedShift(event);
    }

    @KafkaListener(topics = "${spring.kafka.topics.shift-closed}", groupId = "${spring.kafka.consumer.group-id")
    public void handleShiftClosedEvent(ShiftClosedEvent event) {
        log.info("Received shift closed event: {}", event);
        shiftService.saveClosedShift(event);
    }

    @KafkaListener(topics = "${spring.kafka.topics.order-closed}", groupId = "${spring.kafka.consumer.group-id")
    public void handleOrderClosedEvent(OrderClosedEvent event) {
        log.info("Received order closed event: {}", event);
        saleService.saveClosedOrder(event);
    }
}
