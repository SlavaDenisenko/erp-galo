package com.denisenko.orderservice.integration;

import com.denisenko.orderservice.dto.OrderDto;
import com.denisenko.orderservice.dto.OrderItemDto;
import com.denisenko.orderservice.dto.OrderResponse;
import com.denisenko.orderservice.mapper.OrderMapper;
import com.denisenko.orderservice.repository.OrderRepository;
import com.denisenko.orderservice.service.OrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderServiceIntegrationTest extends BaseServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderMapper orderMapper;

    @AfterEach
    void cleanUp() {
        orderRepository.deleteAll();
    }

    @Test
    void createOrderTest() {
        OrderItemDto orderItemDto1 = OrderItemDto.builder()
                .itemId(1)
                .itemName("Pana kota")
                .price(BigDecimal.valueOf(370.0))
                .quantity(1.0)
                .course(1)
                .build();

        OrderItemDto orderItemDto2 = OrderItemDto.builder()
                .itemId(2)
                .itemName("Tiramisu")
                .price(BigDecimal.valueOf(450.0))
                .quantity(1.0)
                .course(1)
                .build();

        OrderDto orderDto = OrderDto.builder()
                .tableNumber(12.0)
                .numberOfGuests(2)
                .items(List.of(orderItemDto1, orderItemDto2))
                .totalPrice(BigDecimal.valueOf(820.0))
                .build();

        String waiterName = "Anton";
        String idempotencyKey = UUID.randomUUID().toString();
        OrderResponse orderResponse = orderService.createOrder(orderDto, waiterName, idempotencyKey);
        assertThat(orderResponse.getOrderId()).isNotNull();
    }
}
