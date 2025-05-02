package com.denisenko.inventoryservice.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecipeCompositionDto {
    private Integer ingredientId;
    @Positive(message = "Quantity must be positive")
    private Double quantity;
}
