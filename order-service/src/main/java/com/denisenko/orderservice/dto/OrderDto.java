package com.denisenko.orderservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {
    private String id;
    @NotNull(message = "Table number is required")
    @Min(value = 1, message = "Table number must be at least 1")
    private Double tableNumber;
    @NotNull(message = "Number of guests is required")
    @Min(value = 1, message = "Number of quests must be at least 1")
    private Integer numberOfGuests;
    private List<OrderItemDto> items;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
    private BigDecimal totalPrice;
    private String waiterName;
    private String paymentMethod;
    private String ofdNumber;
}
