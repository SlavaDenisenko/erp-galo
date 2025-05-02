package com.denisenko.orderservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemDto {
    @NotNull(message = "Item ID is required")
    private Integer itemId;
    private String itemName;
    private BigDecimal price;
    @NotNull(message = "Quantity is required")
    private Double quantity;
    @NotNull(message = "Course is required")
    private Integer course;
    private String status;
    private String location;
    private String reasonForDeletion;
}
