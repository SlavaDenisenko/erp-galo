package com.denisenko.inventoryservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecipeDto {
    private Integer id;
    @NotBlank(message = "Recipe name cannot be blank")
    private String name;
    private String description;
    @NotNull(message = "Category ID cannot be null")
    private Integer categoryId;
    private Integer preparationTimeInMinutes;
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
    @DecimalMin(value = "0")
    @DecimalMax(value = "0")
    private BigDecimal cost;
    private String instructions;
    private List<RecipeCompositionDto> compositions;
    @NotBlank(message = "Recipe location cannot be blank")
    private String location;
}
