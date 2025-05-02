package com.denisenko.orderservice.statemachine;

import com.denisenko.events.OrderClosedItem;
import com.denisenko.events.OrderItemStatus;
import com.denisenko.orderservice.client.FiscalClient;
import com.denisenko.orderservice.config.KafkaTopicsConfig;
import com.denisenko.orderservice.dto.OFDResponse;
import com.denisenko.orderservice.dto.OrderDto;
import com.denisenko.orderservice.dto.OrderItemDto;
import com.denisenko.orderservice.exception.SagaException;
import com.denisenko.orderservice.mapper.OrderMapper;
import com.denisenko.orderservice.repository.OrderRepository;
import com.denisenko.orderservice.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.ZoneId;

import static com.denisenko.orderservice.service.OrderService.IDEMPOTENCY_PREFIX;
import static com.denisenko.orderservice.statemachine.OrderClosedEvent.FAIL;
import static com.denisenko.orderservice.statemachine.OrderClosedState.COMPLETED;
import static com.denisenko.orderservice.statemachine.OrderClosedState.FAILED;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderSagaActions {
    private final RedisCacheService redisCacheService;
    private final FiscalClient fiscalClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicsConfig kafkaTopicsConfig;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public void sendToFiscal(StateContext<OrderClosedState, OrderClosedEvent> ctx) {
        try {
            OrderDto orderDto = (OrderDto) ctx.getExtendedState().getVariables().get("order");
            String idempotencyKey = (String) ctx.getExtendedState().getVariables().get("idempotencyKey");
            OFDResponse ofdResponse = fiscalClient.closeOrder(orderDto, idempotencyKey);

            if (!"SUCCESS".equals(ofdResponse.status())) {
                throw new SagaException("Fiscalization failed for order with ID = " + orderDto.getId() + ". Error: " + ofdResponse.errorMessage());
            }

            orderDto.setOfdNumber(ofdResponse.ofdNumber());
            orderDto.setClosedAt(ofdResponse.timestamp());

            orderRepository.save(orderMapper.toEntity(orderDto));
        } catch (Exception e) {
            ctx.getExtendedState().getVariables().put("error", e);
            ctx.getStateMachine().sendEvent(Mono.just(MessageBuilder.withPayload(FAIL).build())).subscribe();
        }
    }

    public void publishEvent(StateContext<OrderClosedState, OrderClosedEvent> ctx) {
        OrderDto orderDto = (OrderDto) ctx.getExtendedState().getVariables().get("order");
        com.denisenko.events.OrderClosedEvent event = createOrderClosedEvent(orderDto);
        log.info("Preparing to send order closed event: {}", event);
        kafkaTemplate.send(kafkaTopicsConfig.getOrderClosedTopic(), event);
    }

    public void completeAction(StateContext<OrderClosedState, OrderClosedEvent> ctx) {
        String idempotencyKey = (String) ctx.getExtendedState().getVariables().get("idempotencyKey");
        redisCacheService.put(IDEMPOTENCY_PREFIX + idempotencyKey, COMPLETED);
    }

    public void failAction(StateContext<OrderClosedState, OrderClosedEvent> ctx) {
        OrderDto orderDto = (OrderDto) ctx.getExtendedState().getVariables().get("order");
        String idempotencyKey = (String) ctx.getExtendedState().getVariables().get("idempotencyKey");
        Exception e = (Exception) ctx.getExtendedState().getVariables().get("error");
        log.error("Saga failed for order with ID = {}: {}", orderDto.getId(), e != null ? e.getMessage() : "Unknown error");
        redisCacheService.put(IDEMPOTENCY_PREFIX + idempotencyKey, FAILED);
    }

    private com.denisenko.events.OrderClosedEvent createOrderClosedEvent(OrderDto orderDto) {
        com.denisenko.events.OrderClosedEvent event = new com.denisenko.events.OrderClosedEvent();
        event.setTableNumber(orderDto.getTableNumber());
        event.setNumberOfGuests(orderDto.getNumberOfGuests());
        event.setItems(orderDto.getItems().stream().map(this::createOrderClosedItem).toList());
        event.setCreatedAt(orderDto.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant());
        event.setClosedAt(orderDto.getClosedAt().atZone(ZoneId.systemDefault()).toInstant());
        event.setTotalPrice(orderDto.getTotalPrice());
        event.setWaiterName(orderDto.getWaiterName());
        event.setPaymentMethod(orderDto.getPaymentMethod());
        event.setOfdNumber(orderDto.getOfdNumber());
        return event;
    }

    private OrderClosedItem createOrderClosedItem(OrderItemDto orderItem) {
        OrderClosedItem item = new OrderClosedItem();
        item.setItemId(orderItem.getItemId());
        item.setItemName(orderItem.getItemName());
        item.setPrice(orderItem.getPrice());
        item.setQuantity(orderItem.getQuantity());
        item.setCourse(orderItem.getCourse());
        item.setStatus(OrderItemStatus.valueOf(orderItem.getStatus()));
        item.setReasonForDeletion(orderItem.getReasonForDeletion());
        return item;
    }
}
