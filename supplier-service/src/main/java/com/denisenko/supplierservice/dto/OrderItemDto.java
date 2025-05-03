package com.denisenko.supplierservice.dto;

import com.denisenko.supplierservice.model.UnitOfMeasure;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemDto {
    @NotBlank(message = "Product ID is required")
    private String productId;
    private String productName;
    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Quantity must be greater than zero")
    private Double quantity;
    private UnitOfMeasure unitOfMeasure;
}
