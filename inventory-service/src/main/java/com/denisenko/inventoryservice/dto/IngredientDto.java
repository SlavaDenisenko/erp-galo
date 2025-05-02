package com.denisenko.inventoryservice.dto;

import com.denisenko.inventoryservice.model.UnitOfMeasure;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IngredientDto {
    private Integer id;
    @NotBlank(message = "Ingredient name cannot be blank")
    private String name;
    @NotNull(message = "Category ID cannot be null")
    private Integer categoryId;
    @DecimalMin(value = "0")
    @DecimalMax(value = "0")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double quantity;
    @NotNull(message = "Unit of measure cannot be null")
    private UnitOfMeasure unitOfMeasure;
    @Positive(message = "Reorder level must be positive")
    private Double reorderLevel;
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "0.0")
    private BigDecimal cost;
    private String status;
}
