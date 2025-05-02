package com.denisenko.orderservice.integration;

import com.denisenko.orderservice.dto.MenuDto;
import com.denisenko.orderservice.model.Category;
import com.denisenko.orderservice.model.Recipe;
import com.denisenko.orderservice.repository.CategoryRepository;
import com.denisenko.orderservice.repository.RecipeRepository;
import com.denisenko.orderservice.service.MenuService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

public class MenuServiceIntegrationTest extends BaseServiceTest {

    @Autowired
    private MenuService menuService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @BeforeEach
    void setUp() {
        initCategories();
        initRecipes();
    }

    @AfterEach
    void cleanUp() {
        categoryRepository.deleteAll();
        recipeRepository.deleteAll();
    }

    @Test
    void getMenuTest() {
        MenuDto menu = menuService.getMenu();
        Assertions.assertFalse(menu.getCategories().isEmpty());
    }

    private void initCategories() {
        List<Category> categories = List.of(
                new Category(1, "Salads", null),
                new Category(2, "Main Courses", null),
                new Category(3, "Desserts", null),
                new Category(4, "Soups", null),
                new Category(5, "Beverages", null),
                new Category(6, "Additions", 3)
        );
        categoryRepository.saveAll(categories);
    }

    private void initRecipes() {
        List<Recipe> recipes = List.of(
                new Recipe(1, "Caesar Salad", null, 1, null, new BigDecimal(650), null, "kitchen"),
                new Recipe(2, "Beef Stew", null, 2, null, new BigDecimal(990), null, "kitchen"),
                new Recipe(3, "Shrimp Soup", null, 4, null, new BigDecimal(750), null, "kitchen"),
                new Recipe(4, "Apple Pie", null, 3, null, new BigDecimal(500), null, "kitchen"),
                new Recipe(5, "Banana Bread", null, 3, null, new BigDecimal(450), null, "kitchen"),
                new Recipe(6, "Omelette", null, 2, null, new BigDecimal(350), null, "kitchen"),
                new Recipe(7, "Lemon Tea", null, 5, null, new BigDecimal(200), null, "bar"),
                new Recipe(8, "Chocolate", null, 6, null, new BigDecimal(270), null, "kitchen")
        );
        recipeRepository.saveAll(recipes);
    }
}
