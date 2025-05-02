package com.denisenko.inventoryservice.unit.validation;

import com.denisenko.inventoryservice.dto.CategoryDto;
import com.denisenko.inventoryservice.model.CategoryType;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CategoryDtoValidationTest extends BaseValidationTest {

    @Test
    void validCategoryTest() {
        CategoryDto categoryDto = CategoryDto.builder()
                .name("Starters")
                .description("This section presents light snacks")
                .parentCategoryId(1)
                .categoryType(CategoryType.RECIPE)
                .build();

        Set<ConstraintViolation<CategoryDto>> violations = validator.validate(categoryDto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void missingNameTest() {
        CategoryDto categoryDto = CategoryDto.builder()
                .description("This section presents light snacks")
                .parentCategoryId(1)
                .categoryType(CategoryType.RECIPE)
                .build();

        Set<ConstraintViolation<CategoryDto>> violations = validator.validate(categoryDto);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Category name cannot be blank", violations.iterator().next().getMessage());
    }

    @Test
    void missingCategoryTypeTest() {
        CategoryDto categoryDto = CategoryDto.builder()
                .name("Starters")
                .description("This section presents light snacks")
                .parentCategoryId(1)
                .build();

        Set<ConstraintViolation<CategoryDto>> violations = validator.validate(categoryDto);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Category type cannot be null", violations.iterator().next().getMessage());
    }
}
