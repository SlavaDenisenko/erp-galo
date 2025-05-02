package com.denisenko.inventoryservice.integration;

import com.denisenko.inventoryservice.dto.IngredientDto;
import com.denisenko.inventoryservice.model.*;
import com.denisenko.inventoryservice.repository.CategoryRepository;
import com.denisenko.inventoryservice.repository.IngredientRepository;
import com.denisenko.inventoryservice.repository.RecipeRepository;
import com.denisenko.inventoryservice.service.IngredientService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.denisenko.inventoryservice.model.IngredientStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;

public class IngredientServiceIntegrationTest extends BaseServiceTest {

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @BeforeEach
    public void setUp() {
        initIngredients();
    }

    @AfterEach
    public void cleanUp() {
        recipeRepository.deleteAll();
        ingredientRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void createIngredientTest() {
        IngredientCategory category = addSingleCategory();
        IngredientDto ingredientDto = IngredientDto.builder()
                .name("Beef")
                .categoryId(category.getId())
                .unitOfMeasure(UnitOfMeasure.KILOGRAM)
                .reorderLevel(3.0)
                .build();

        String idempotencyKey = UUID.randomUUID().toString();
        IngredientDto ingredient = ingredientService.createIngredient(ingredientDto, idempotencyKey);
        assertThat(ingredient).isNotNull();
        assertThat(ingredient.getName()).isEqualTo(ingredientDto.getName());
        assertThat(ingredient.getCategoryId()).isEqualTo(ingredientDto.getCategoryId());
        assertThat(ingredient.getCost().doubleValue()).isEqualTo(BigDecimal.ZERO.doubleValue());

        List<Ingredient> ingredients = ingredientRepository.findAll();
        assertThat(ingredients).hasSize(5);
        assertThat(ingredients.get(4).getName()).isEqualTo(ingredientDto.getName());
        assertThat(ingredients.get(4).getIngredientCategory().getId()).isEqualTo(ingredientDto.getCategoryId());
        assertThat(ingredients.get(4).getCost().doubleValue()).isEqualTo(BigDecimal.ZERO.doubleValue());
        assertThat(ingredients.get(4).getCreatedAt()).isNotNull();
    }

    @Test
    void getAllIngredientsTest() {
        List<IngredientDto> ingredients = ingredientService.getAllIngredients();
        assertThat(ingredients).hasSize(4);
    }

    @Test
    void getIngredientTest() {
        Ingredient ingredient = addSingleIngredient();
        IngredientDto receivedIngredient = ingredientService.getIngredient(ingredient.getId());
        assertThat(receivedIngredient).isNotNull();
        assertThat(receivedIngredient.getName()).isEqualTo(ingredient.getName());
        assertThat(receivedIngredient.getCategoryId()).isEqualTo(ingredient.getIngredientCategory().getId());
        assertThat(receivedIngredient.getUnitOfMeasure()).isEqualTo(ingredient.getUnitOfMeasure());
        assertThat(receivedIngredient.getCost().doubleValue()).isEqualTo(ingredient.getCost().doubleValue());

        Optional<Ingredient> byId = ingredientRepository.findById(ingredient.getId());
        if (byId.isPresent()) {
            assertThat(byId.get().getId()).isEqualTo(receivedIngredient.getId());
            assertThat(byId.get().getName()).isEqualTo(receivedIngredient.getName());
        }
    }

    @Test
    void updateIngredientTest() {
        Ingredient ingredient = addSingleIngredient();
        IngredientDto ingredientDto = IngredientDto.builder()
                .name("Banana Milk")
                .categoryId(ingredient.getIngredientCategory().getId())
                .reorderLevel(50.0)
                .build();
        IngredientDto updatedIngredient = ingredientService.updateIngredient(ingredient.getId(), ingredientDto);

        assertThat(updatedIngredient).isNotNull();
        assertThat(updatedIngredient.getName()).isEqualTo(ingredientDto.getName());
        assertThat(updatedIngredient.getCategoryId()).isEqualTo(ingredientDto.getCategoryId());
        assertThat(updatedIngredient.getReorderLevel()).isEqualTo(ingredientDto.getReorderLevel());

        List<Ingredient> ingredients = ingredientRepository.findAll();
        assertThat(ingredients).hasSize(5);
        Optional<Ingredient> byId = ingredientRepository.findById(ingredient.getId());
        if (byId.isPresent()) {
            assertThat(byId.get().getName()).isEqualTo(ingredientDto.getName());
            assertThat(byId.get().getIngredientCategory().getId()).isEqualTo(ingredientDto.getCategoryId());
            assertThat(byId.get().getReorderLevel()).isEqualTo(ingredientDto.getReorderLevel());
            assertThat(byId.get().getUpdatedAt()).isNotNull();
        }
    }

    private Ingredient addSingleIngredient() {
        IngredientCategory category = addSingleCategory();
        Ingredient ingredient = Ingredient.builder()
                .name("Milk")
                .ingredientCategory(category)
                .status(ACTIVE)
                .unitOfMeasure(UnitOfMeasure.LITER)
                .cost(new BigDecimal(100))
                .build();
        return ingredientRepository.save(ingredient);
    }

    private IngredientCategory addSingleCategory() {
        IngredientCategory category = new IngredientCategory();
        category.setName("Drinks");
        return categoryRepository.save(category);
    }

    private Ingredient addIngredientAndRecipe() {
        Ingredient ingredient = addSingleIngredient();

        RecipeComposition composition = new RecipeComposition();
        composition.setIngredient(ingredient);
        composition.setQuantity(4.0);

        RecipeCategory category = new RecipeCategory();
        category.setName("Desserts");
        categoryRepository.save(category);

        Recipe recipe = new Recipe();
        recipe.setName("Pana kota");
        recipe.setRecipeCategory(category);
        recipe.setPrice(new BigDecimal(600));
        recipe.setRecipeCompositions(List.of(composition));
        recipeRepository.save(recipe);
        return ingredient;
    }

    private void initIngredients() {
        IngredientCategory category1 = new IngredientCategory();
        category1.setName("Vegetables");
        IngredientCategory category2 = new IngredientCategory();
        category2.setName("Other");
        categoryRepository.saveAll(List.of(category1, category2));

        Ingredient ingredient1 = Ingredient.builder()
                .name("Sugar")
                .ingredientCategory(category2)
                .status(ACTIVE)
                .unitOfMeasure(UnitOfMeasure.GRAM)
                .cost(new BigDecimal(40))
                .build();

        Ingredient ingredient2 = Ingredient.builder()
                .name("Carrot")
                .ingredientCategory(category1)
                .status(ACTIVE)
                .unitOfMeasure(UnitOfMeasure.GRAM)
                .cost(new BigDecimal(35))
                .build();

        Ingredient ingredient3 = Ingredient.builder()
                .name("Potato")
                .ingredientCategory(category1)
                .status(ACTIVE)
                .unitOfMeasure(UnitOfMeasure.GRAM)
                .cost(new BigDecimal(15))
                .build();

        Ingredient ingredient4 = Ingredient.builder()
                .name("Egg plant")
                .ingredientCategory(category1)
                .status(ACTIVE)
                .unitOfMeasure(UnitOfMeasure.GRAM)
                .cost(new BigDecimal(80))
                .build();

        List<Ingredient> ingredients = List.of(ingredient1, ingredient2, ingredient3, ingredient4);
        ingredientRepository.saveAll(ingredients);
    }
}
