package com.denisenko.orderservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RedisHash("Order")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {

    @Id
    private String id;
    private Double tableNumber;
    private Integer numberOfGuests;
    private List<OrderItem> items;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
    private BigDecimal totalPrice;
    private String waiterName;
    private PaymentMethod paymentMethod;
    private String ofdNumber;
}
