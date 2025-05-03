package com.denisenko.inventoryservice.unit.validation;

import com.denisenko.inventoryservice.dto.IngredientDto;
import com.denisenko.inventoryservice.model.UnitOfMeasure;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class IngredientDtoValidationTest extends BaseValidationTest {

    @Test
    void validIngredientTest() {
        IngredientDto ingredientDto = IngredientDto.builder()
                .name("Beef")
                .categoryId(5)
                .unitOfMeasure(UnitOfMeasure.KILOGRAM)
                .reorderLevel(3.0)
                .build();

        Set<ConstraintViolation<IngredientDto>> violations = validator.validate(ingredientDto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidQuantityTest() {
        IngredientDto ingredientDto = IngredientDto.builder()
                .name("Carrot")
                .categoryId(3)
                .quantity(2.0)
                .unitOfMeasure(UnitOfMeasure.GRAM)
                .reorderLevel(5000.0)
                .build();

        Set<ConstraintViolation<IngredientDto>> violations = validator.validate(ingredientDto);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void invalidReorderLevel() {
        IngredientDto ingredientDto = IngredientDto.builder()
                .name("Milk")
                .categoryId(10)
                .quantity(0.0)
                .unitOfMeasure(UnitOfMeasure.LITER)
                .reorderLevel(-100.0)
                .build();

        Set<ConstraintViolation<IngredientDto>> violations = validator.validate(ingredientDto);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Reorder level must be positive", violations.iterator().next().getMessage());
    }

    @Test
    void invalidCostTest() {
        IngredientDto ingredientDto = IngredientDto.builder()
                .name("Apple Juice")
                .categoryId(11)
                .quantity(0.0)
                .unitOfMeasure(UnitOfMeasure.LITER)
                .reorderLevel(100.0)
                .cost(new BigDecimal(100))
                .build();

        Set<ConstraintViolation<IngredientDto>> violations = validator.validate(ingredientDto);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }
}
