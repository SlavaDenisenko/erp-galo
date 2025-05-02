package com.denisenko.inventoryservice.data;

import com.denisenko.inventoryservice.dto.*;
import com.denisenko.inventoryservice.model.CategoryType;
import com.denisenko.inventoryservice.model.DocumentType;
import com.denisenko.inventoryservice.model.MovementType;
import com.denisenko.inventoryservice.model.UnitOfMeasure;
import com.denisenko.inventoryservice.service.CategoryService;
import com.denisenko.inventoryservice.service.DocumentService;
import com.denisenko.inventoryservice.service.IngredientService;
import com.denisenko.inventoryservice.service.RecipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Map.entry;

@Profile("!test")
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {
    private final CategoryService categoryService;
    private final IngredientService ingredientService;
    private final RecipeService recipeService;
    private final DocumentService documentService;

    private final Map<String, Integer> ingredientCategoryMap = new HashMap<>();
    private final Map<String, Integer> recipeCategoryMap = new HashMap<>();
    private final Map<String, Integer> ingredientMap = new HashMap<>();

    @Override
    public void run(ApplicationArguments args) {
        try {
            createInitialCategories();
            createInitialIngredients();
            createInitialRecipes();
            createInitialDocuments();
        } catch (Exception e) {
            log.error("Error while initializing data", e);
        }
    }

    private void createInitialCategories() {
        Map<String, CategoryType> categories = Map.ofEntries(
                entry("Fruits", CategoryType.INGREDIENT),
                entry("Meats", CategoryType.INGREDIENT),
                entry("Dairy", CategoryType.INGREDIENT),
                entry("Spices", CategoryType.INGREDIENT),
                entry("Bakery", CategoryType.INGREDIENT),
                entry("Drinks", CategoryType.INGREDIENT),
                entry("Seafood", CategoryType.INGREDIENT),

                entry("Salads", CategoryType.RECIPE),
                entry("Main Courses", CategoryType.RECIPE),
                entry("Desserts", CategoryType.RECIPE),
                entry("Soups", CategoryType.RECIPE),
                entry("Beverages", CategoryType.RECIPE)
        );

        categories.forEach((name, type) -> {
            CategoryDto categoryDto = CategoryDto.builder()
                    .name(name)
                    .categoryType(type)
                    .parentCategoryId(null)
                    .build();
            String idempotencyKey = UUID.randomUUID().toString();
            CategoryDto category = categoryService.createCategory(categoryDto, idempotencyKey);
            if (type == CategoryType.INGREDIENT) {
                ingredientCategoryMap.put(name, category.getId());
            } else {
                recipeCategoryMap.put(name, category.getId());
            }
        });
    }

    private void createInitialIngredients() {
        record IngredientData(String name, String category, UnitOfMeasure unitOfMeasure, Double reorderLevel) {
        }

        List<IngredientData> ingredients = List.of(
                new IngredientData("Chicken Breast", "Meats", UnitOfMeasure.KILOGRAM, 5.0),
                new IngredientData("Beef", "Meats", UnitOfMeasure.KILOGRAM, 4.0),
                new IngredientData("Milk", "Dairy", UnitOfMeasure.LITER, 3.0),
                new IngredientData("Egg", "Dairy", UnitOfMeasure.PIECE, 30.0),
                new IngredientData("Cheese", "Dairy", UnitOfMeasure.KILOGRAM, 2.0),
                new IngredientData("Apple", "Fruits", UnitOfMeasure.KILOGRAM, 3.0),
                new IngredientData("Banana", "Fruits", UnitOfMeasure.KILOGRAM, 2.0),
                new IngredientData("Cinnamon", "Spices", UnitOfMeasure.GRAM, 500.0),
                new IngredientData("Black Pepper", "Spices", UnitOfMeasure.GRAM, 300.0),
                new IngredientData("Flour", "Bakery", UnitOfMeasure.KILOGRAM, 10.0),
                new IngredientData("Yeast", "Bakery", UnitOfMeasure.GRAM, 200.0),
                new IngredientData("Shrimp", "Seafood", UnitOfMeasure.KILOGRAM, 2.0),
                new IngredientData("Salmon", "Seafood", UnitOfMeasure.KILOGRAM, 2.0),
                new IngredientData("Lemon", "Fruits", UnitOfMeasure.KILOGRAM, 2.0),
                new IngredientData("Water", "Drinks", UnitOfMeasure.LITER, 20.0),
                new IngredientData("Tea Leaves", "Drinks", UnitOfMeasure.GRAM, 300.0)
        );

        ingredients.forEach(data -> {
            IngredientDto ingredientDto = IngredientDto.builder()
                    .name(data.name())
                    .categoryId(ingredientCategoryMap.get(data.category()))
                    .unitOfMeasure(data.unitOfMeasure())
                    .reorderLevel(data.reorderLevel())
                    .build();
            String idempotencyKey = UUID.randomUUID().toString();
            IngredientDto ingredient = ingredientService.createIngredient(ingredientDto, idempotencyKey);
            ingredientMap.put(ingredient.getName(), ingredient.getId());
        });
    }

    private void createInitialRecipes() {
        record RecipeData(String name, String category, BigDecimal price, Map<String, Double> compositions,
                          String location) {
        }

        List<RecipeData> recipes = List.of(
                new RecipeData("Caesar Salad", "Salads", new BigDecimal(650), Map.of(
                        "Chicken Breast", 0.3,
                        "Cheese", 0.1,
                        "Black Pepper", 5.0
                ), "kitchen"),
                new RecipeData("Beef Stew", "Main Courses", new BigDecimal(990), Map.of(
                        "Beef", 0.4,
                        "Black Pepper", 3.0
                ), "kitchen"),
                new RecipeData("Shrimp Soup", "Soups", new BigDecimal(750), Map.of(
                        "Shrimp", 0.3,
                        "Lemon", 0.1,
                        "Black Pepper", 2.0
                ), "kitchen"),
                new RecipeData("Apple Pie", "Desserts", new BigDecimal(500), Map.of(
                        "Apple", 0.3,
                        "Flour", 0.2,
                        "Cinnamon", 2.0
                ), "kitchen"),
                new RecipeData("Banana Bread", "Desserts", new BigDecimal(450), Map.of(
                        "Banana", 0.4,
                        "Flour", 0.3,
                        "Yeast", 1.5
                ), "kitchen"),
                new RecipeData("Omelette", "Main Courses", new BigDecimal(350), Map.of(
                        "Egg", 3.0,
                        "Milk", 0.1,
                        "Cheese", 0.05
                ), "kitchen"),
                new RecipeData("Lemon Tea", "Beverages", new BigDecimal(200), Map.of(
                        "Tea Leaves", 3.0,
                        "Lemon", 0.1,
                        "Water", 0.25
                ), "bar")
        );

        recipes.forEach(data -> {
            List<RecipeCompositionDto> compositions = data.compositions().entrySet().stream()
                    .map(e -> new RecipeCompositionDto(ingredientMap.get(e.getKey()), e.getValue()))
                    .toList();

            RecipeDto recipeDto = RecipeDto.builder()
                    .name(data.name())
                    .categoryId(recipeCategoryMap.get(data.category()))
                    .price(data.price())
                    .compositions(compositions)
                    .location(data.location())
                    .build();
            String idempotencyKey = UUID.randomUUID().toString();
            recipeService.createRecipe(recipeDto, idempotencyKey);
        });
    }

    private void createInitialDocuments() {
        LocalDateTime movementDate = LocalDateTime.now().minusDays(2);

        List<InventoryMovementDto> movements = List.of(
                InventoryMovementDto.builder()
                        .ingredientId(ingredientMap.get("Chicken Breast"))
                        .quantity(5.0)
                        .cost(new BigDecimal(500))
                        .movementType(MovementType.RECEIVED.name())
                        .movementDate(movementDate)
                        .build(),
                InventoryMovementDto.builder()
                        .ingredientId(ingredientMap.get("Beef"))
                        .quantity(4.0)
                        .cost(new BigDecimal(900))
                        .movementType(MovementType.RECEIVED.name())
                        .movementDate(movementDate)
                        .build(),
                InventoryMovementDto.builder()
                        .ingredientId(ingredientMap.get("Milk"))
                        .quantity(10.0)
                        .cost(new BigDecimal(120))
                        .movementType(MovementType.RECEIVED.name())
                        .movementDate(movementDate)
                        .build(),
                InventoryMovementDto.builder()
                        .ingredientId(ingredientMap.get("Egg"))
                        .quantity(50.0)
                        .cost(new BigDecimal(20))
                        .movementType(MovementType.RECEIVED.name())
                        .movementDate(movementDate)
                        .build(),
                InventoryMovementDto.builder()
                        .ingredientId(ingredientMap.get("Cheese"))
                        .quantity(3.0)
                        .cost(new BigDecimal(450))
                        .movementType(MovementType.RECEIVED.name())
                        .movementDate(movementDate)
                        .build(),
                InventoryMovementDto.builder()
                        .ingredientId(ingredientMap.get("Apple"))
                        .quantity(6.0)
                        .cost(new BigDecimal(110))
                        .movementType(MovementType.RECEIVED.name())
                        .movementDate(movementDate)
                        .build(),
                InventoryMovementDto.builder()
                        .ingredientId(ingredientMap.get("Banana"))
                        .quantity(4.0)
                        .cost(new BigDecimal(100))
                        .movementType(MovementType.RECEIVED.name())
                        .movementDate(movementDate)
                        .build(),
                InventoryMovementDto.builder()
                        .ingredientId(ingredientMap.get("Cinnamon"))
                        .quantity(0.2)
                        .cost(new BigDecimal(2000))
                        .movementType(MovementType.RECEIVED.name())
                        .movementDate(movementDate)
                        .build(),
                InventoryMovementDto.builder()
                        .ingredientId(ingredientMap.get("Black Pepper"))
                        .quantity(0.3)
                        .cost(new BigDecimal(1800))
                        .movementType(MovementType.RECEIVED.name())
                        .movementDate(movementDate)
                        .build(),
                InventoryMovementDto.builder()
                        .ingredientId(ingredientMap.get("Flour"))
                        .quantity(10.0)
                        .cost(new BigDecimal(80))
                        .movementType(MovementType.RECEIVED.name())
                        .movementDate(movementDate)
                        .build(),
                InventoryMovementDto.builder()
                        .ingredientId(ingredientMap.get("Yeast"))
                        .quantity(0.2)
                        .cost(new BigDecimal(2500))
                        .movementType(MovementType.RECEIVED.name())
                        .movementDate(movementDate)
                        .build(),
                InventoryMovementDto.builder()
                        .ingredientId(ingredientMap.get("Shrimp"))
                        .quantity(2.5)
                        .cost(new BigDecimal(1200))
                        .movementType(MovementType.RECEIVED.name())
                        .movementDate(movementDate)
                        .build(),
                InventoryMovementDto.builder()
                        .ingredientId(ingredientMap.get("Salmon"))
                        .quantity(1.5)
                        .cost(new BigDecimal(1400))
                        .movementType(MovementType.RECEIVED.name())
                        .movementDate(movementDate)
                        .build(),
                InventoryMovementDto.builder()
                        .ingredientId(ingredientMap.get("Lemon"))
                        .quantity(3.0)
                        .cost(new BigDecimal(100))
                        .movementType(MovementType.RECEIVED.name())
                        .movementDate(movementDate)
                        .build(),
                InventoryMovementDto.builder()
                        .ingredientId(ingredientMap.get("Water"))
                        .quantity(30.0)
                        .cost(new BigDecimal(10))
                        .movementType(MovementType.RECEIVED.name())
                        .movementDate(movementDate)
                        .build(),
                InventoryMovementDto.builder()
                        .ingredientId(ingredientMap.get("Tea Leaves"))
                        .quantity(0.5)
                        .cost(new BigDecimal(1500))
                        .movementType(MovementType.RECEIVED.name())
                        .movementDate(movementDate)
                        .build()
        );

        DocumentDto documentDto = DocumentDto.builder()
                .documentNumber("DOC-001")
                .documentDate(LocalDate.now())
                .documentType(DocumentType.SUPPLY.name())
                .supplierName("")
                .inventoryMovements(movements)
                .build();
        String idempotencyKey = UUID.randomUUID().toString();
        documentService.createDocument(documentDto, idempotencyKey);
    }
}
