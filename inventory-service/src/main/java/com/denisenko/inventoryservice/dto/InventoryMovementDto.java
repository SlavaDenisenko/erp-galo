package com.denisenko.inventoryservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryMovementDto {
    private Integer id;
    @NotNull(message = "Ingredient ID cannot be null")
    private Integer ingredientId;
    @NotNull(message = "Quantity cannot be null")
    @Positive(message = "Quantity must be positive")
    private Double quantity;
    @NotNull(message = "Cost cannot be null")
    @Positive(message = "Cost must be positive")
    private BigDecimal cost;
    @NotNull(message = "Movement type cannot be null")
    private String movementType;
    @NotNull(message = "Movement date cannot be null")
    private LocalDateTime movementDate;
    @NotNull(message = "Associated document ID cannot be null")
    private Integer associatedDocumentId;
}
