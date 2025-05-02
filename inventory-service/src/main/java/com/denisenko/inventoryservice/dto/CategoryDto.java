package com.denisenko.inventoryservice.dto;

import com.denisenko.inventoryservice.model.CategoryType;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryDto {
    private Integer id;
    @NotBlank(message = "Category name cannot be blank")
    private String name;
    private String description;
    private Integer parentCategoryId;
    @NotNull(message = "Category type cannot be null")
    private CategoryType categoryType;
    private List<CategoryDto> children;
    private List<IngredientDto> ingredients;
    private List<RecipeDto> recipes;
}
