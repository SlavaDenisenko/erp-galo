package com.denisenko.inventoryservice.integration;

import com.denisenko.inventoryservice.dto.CategoryDto;
import com.denisenko.inventoryservice.model.CategoryType;
import com.denisenko.inventoryservice.model.Category;
import com.denisenko.inventoryservice.model.IngredientCategory;
import com.denisenko.inventoryservice.model.RecipeCategory;
import com.denisenko.inventoryservice.repository.CategoryRepository;
import com.denisenko.inventoryservice.service.CategoryService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CategoryServiceIntegrationTest extends BaseServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    public void setUp() {
        initCategories();
    }

    @AfterEach
    public void cleanUp() {
        categoryRepository.deleteAll();
    }

    @Test
    void createCategoryTest() {
        CategoryDto categoryDto = CategoryDto.builder()
                .name("Citrus")
                .description("This ingredient category is intended for citrus fruits")
                .parentCategoryId(2)
                .categoryType(CategoryType.INGREDIENT)
                .build();

        String idempotencyKey = UUID.randomUUID().toString();
        CategoryDto category = categoryService.createCategory(categoryDto, idempotencyKey);
        assertThat(category).isNotNull();
        assertThat(category.getName()).isEqualTo("Citrus");
        assertThat(category.getCategoryType()).isEqualTo(CategoryType.INGREDIENT);

        List<Category> categories = categoryRepository.findAll();
        assertThat(categories).hasSize(5);
        assertThat(categories.get(4).getName()).isEqualTo("Citrus");
        assertThat(categories.get(4).getCategoryType()).isEqualTo(CategoryType.INGREDIENT);
    }

    @Test
    void getAllCategoriesTest() {
        CategoryDto allIngredientCategories = categoryService.getAllCategories(CategoryType.INGREDIENT);
        assertThat(allIngredientCategories.getChildren()).hasSize(2);

        CategoryDto allRecipeCategories = categoryService.getAllCategories(CategoryType.RECIPE);
        assertThat(allRecipeCategories.getChildren()).hasSize(1);
        assertThat(allRecipeCategories.getChildren().get(0).getChildren()).hasSize(1);
    }

    @Test
    void updateCategoryTest() {
        Category category = addSingleIngredientCategory();
        CategoryDto updatedCategoryDto = CategoryDto.builder()
                .name("Tropic")
                .description("This ingredient category is intended for tropic fruits")
                .categoryType(CategoryType.INGREDIENT)
                .build();
        CategoryDto updatedCategory = categoryService.updateCategory(category.getId(), updatedCategoryDto);

        assertThat(updatedCategory).isNotNull();
        assertThat(updatedCategory.getName()).isEqualTo("Tropic");
        assertThat(updatedCategory.getDescription()).isEqualTo("This ingredient category is intended for tropic fruits");

        List<Category> categories = categoryRepository.findAll();
        assertThat(categories).hasSize(5);
        assertThat(categories.get(4).getName()).isEqualTo("Tropic");
        assertThat(categories.get(4).getDescription()).isEqualTo("This ingredient category is intended for tropic fruits");
    }

    @Test
    @Transactional
    void deleteCategoryTest() {
        Category addedCategory = addSingleIngredientCategory();
        List<Category> categoriesBeforeDeletion = categoryRepository.findAll();
        assertThat(categoriesBeforeDeletion).hasSize(5);

        boolean deleted = categoryService.deleteCategory(addedCategory.getId());
        List<Category> categoriesAfterDeletion = categoryRepository.findAll();
        assertTrue(deleted);
        assertThat(categoriesAfterDeletion).hasSize(4);

        Optional<Category> notEmptyCategory = categoriesAfterDeletion.stream().filter(Category::hasContent).findAny();
        if (notEmptyCategory.isPresent()) {
            boolean notDeleted = categoryService.deleteCategory(notEmptyCategory.get().getId());
            assertFalse(notDeleted);
            assertThat(categoriesAfterDeletion).hasSize(4);
        }
    }

    private Category addSingleIngredientCategory() {
        IngredientCategory category = new IngredientCategory();
        category.setName("Citrus");
        category.setDescription("This ingredient category is intended for citrus fruits");
        category.setCategoryType(CategoryType.INGREDIENT);
        category.setCreatedAt(LocalDateTime.of(2024, Month.AUGUST, 29, 12, 33));
        return categoryRepository.save(category);
    }

    private void initCategories() {
        IngredientCategory category1 = new IngredientCategory();
        category1.setName("Vegetables");
        category1.setDescription("This ingredient category is intended for vegetables");
        category1.setCategoryType(CategoryType.INGREDIENT);
        category1.setCreatedAt(LocalDateTime.of(2024, Month.SEPTEMBER, 16, 9, 26));

        IngredientCategory category2 = new IngredientCategory();
        category2.setName("Fruits");
        category2.setDescription("This ingredient category is intended for fruits");
        category2.setCategoryType(CategoryType.INGREDIENT);
        category2.setCreatedAt(LocalDateTime.of(2024, Month.AUGUST, 28, 13, 15));

        RecipeCategory category3 = new RecipeCategory();
        category3.setName("Desserts");
        category3.setDescription("This recipe category is intended for desserts");
        category3.setCategoryType(CategoryType.RECIPE);
        category3.setCreatedAt(LocalDateTime.of(2024, Month.SEPTEMBER, 2, 16, 50));

        RecipeCategory category4 = new RecipeCategory();
        category4.setName("Additions");
        category4.setDescription("This recipe category is intended for additions to desserts");
        category4.setCategoryType(CategoryType.RECIPE);
        category4.setParent(category3);
        category4.setCreatedAt(LocalDateTime.of(2024, Month.SEPTEMBER, 4, 13, 45));

        List<Category> categories = List.of(category1, category2, category3, category4);
        categoryRepository.saveAll(categories);
    }
}
