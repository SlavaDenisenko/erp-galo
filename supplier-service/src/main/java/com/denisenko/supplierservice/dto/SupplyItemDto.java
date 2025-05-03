package com.denisenko.supplierservice.dto;

import com.denisenko.supplierservice.model.UnitOfMeasure;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupplyItemDto {
    @NotBlank(message = "Product ID is required")
    private String productId;
    @NotBlank(message = "Product name is required")
    private String productName;
    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Quantity must be greater than zero")
    private Double quantity;
    @NotNull(message = "Cost is required")
    private BigDecimal cost;
    @NotNull(message = "Unit of measure cannot be null")
    private UnitOfMeasure unitOfMeasure;
}
