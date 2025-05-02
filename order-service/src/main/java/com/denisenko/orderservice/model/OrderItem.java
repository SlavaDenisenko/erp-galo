package com.denisenko.orderservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItem {
    private Integer itemId;
    private String itemName;
    private BigDecimal price;
    private Double quantity;
    private Integer course;
    private OrderItemStatus status;
    private String reasonForDeletion;
    private String location;
}
