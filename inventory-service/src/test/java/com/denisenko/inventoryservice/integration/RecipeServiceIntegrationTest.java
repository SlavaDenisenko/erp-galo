package com.denisenko.inventoryservice.integration;

import com.denisenko.inventoryservice.dto.RecipeCompositionDto;
import com.denisenko.inventoryservice.dto.RecipeDto;
import com.denisenko.inventoryservice.model.*;
import com.denisenko.inventoryservice.repository.CategoryRepository;
import com.denisenko.inventoryservice.repository.IngredientRepository;
import com.denisenko.inventoryservice.repository.RecipeRepository;
import com.denisenko.inventoryservice.service.KafkaProducerService;
import com.denisenko.inventoryservice.service.RecipeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.denisenko.inventoryservice.model.IngredientStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;

public class RecipeServiceIntegrationTest extends BaseServiceTest {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @MockBean
    private KafkaProducerService kafkaProducerService;

    @BeforeEach
    void setUp() {
        initRecipes();
    }

    @AfterEach
    public void cleanUp() {
        recipeRepository.deleteAll();
        ingredientRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    @Transactional
    void createRecipeTest() {
        RecipeCategory category = addSingleCategory();
        Ingredient ingredient = addSingleIngredient();

        RecipeCompositionDto composition = RecipeCompositionDto.builder()
                .ingredientId(ingredient.getId())
                .quantity(5.0)
                .build();
        RecipeDto recipeDto = RecipeDto.builder()
                .name("Pasta Alfredo")
                .description("The classic Italian pasta consists of ...")
                .categoryId(category.getId())
                .price(new BigDecimal(800))
                .compositions(List.of(composition))
                .location("kitchen")
                .build();

        String idempotencyKey = UUID.randomUUID().toString();
        RecipeDto recipe = recipeService.createRecipe(recipeDto, idempotencyKey);
        assertThat(recipe).isNotNull();
        assertThat(recipe.getName()).isEqualTo(recipeDto.getName());
        assertThat(recipe.getDescription()).isEqualTo(recipeDto.getDescription());
        assertThat(recipe.getCategoryId()).isEqualTo(recipeDto.getCategoryId());
        assertThat(recipe.getPrice()).isEqualTo(recipeDto.getPrice());
        assertThat(recipe.getCompositions()).isEqualTo(recipeDto.getCompositions());

        List<Recipe> recipes = recipeRepository.findAll();
        assertThat(recipes).hasSize(3);
        assertThat(recipes.get(2).getName()).isEqualTo(recipeDto.getName());
        assertThat(recipes.get(2).getDescription()).isEqualTo(recipeDto.getDescription());
        assertThat(recipes.get(2).getRecipeCategory().getId()).isEqualTo(recipeDto.getCategoryId());
        assertThat(recipes.get(2).getPrice().doubleValue()).isEqualTo(recipeDto.getPrice().doubleValue());
        assertThat(recipes.get(2).getRecipeCompositions().size()).isEqualTo(recipeDto.getCompositions().size());
        assertThat(recipes.get(2).getCreatedAt()).isNotNull();

        BigDecimal cost = BigDecimal.ZERO;
        for (RecipeCompositionDto recipeComposition : recipeDto.getCompositions()) {
            Optional<Ingredient> byId = ingredientRepository.findById(recipeComposition.getIngredientId());
            if (byId.isPresent()) {
                cost = cost.add(byId.get().getCost().multiply(BigDecimal.valueOf(recipeComposition.getQuantity())));
            }
        }
        assertThat(recipes.get(2).getCost()).isEqualTo(cost);
    }

    @Test
    void getAllRecipesTest() {
        List<RecipeDto> recipes = recipeService.getRecipes();
        assertThat(recipes).hasSize(2);
    }

    @Test
    void getRecipeCostsTest() {
        List<Integer> recipeIds = recipeService.getRecipes().stream()
                .map(RecipeDto::getId)
                .toList();
        Map<Integer, BigDecimal> recipeCosts = recipeService.getRecipeCosts(recipeIds);
        Assertions.assertNotNull(recipeCosts);
        assertThat(recipeCosts).hasSize(2);
    }

    private RecipeCategory addSingleCategory() {
        RecipeCategory category = new RecipeCategory();
        category.setName("Pasta & risotto");
        return categoryRepository.save(category);
    }

    private Ingredient addSingleIngredient() {
        IngredientCategory category = new IngredientCategory();
        category.setName("Pasta");
        categoryRepository.save(category);

        Ingredient ingredient = Ingredient.builder()
                .name("Fettuccine")
                .ingredientCategory(category)
                .unitOfMeasure(UnitOfMeasure.GRAM)
                .cost(new BigDecimal(100))
                .status(ACTIVE)
                .build();
        return ingredientRepository.save(ingredient);
    }

    private void initRecipes() {
        IngredientCategory category1 = new IngredientCategory();
        category1.setName("Vegetables");
        IngredientCategory category2 = new IngredientCategory();
        category2.setName("Other");
        RecipeCategory category3 = new RecipeCategory();
        category3.setName("Main Course");
        RecipeCategory category4 = new RecipeCategory();
        category4.setName("OGGI");
        categoryRepository.saveAll(List.of(category1, category2, category3, category4));

        Ingredient ingredient1 = Ingredient.builder()
                .name("Sugar")
                .ingredientCategory(category2)
                .unitOfMeasure(UnitOfMeasure.GRAM)
                .cost(new BigDecimal(40))
                .status(ACTIVE)
                .build();

        Ingredient ingredient2 = Ingredient.builder()
                .name("Carrot")
                .ingredientCategory(category1)
                .unitOfMeasure(UnitOfMeasure.GRAM)
                .cost(new BigDecimal(35))
                .status(ACTIVE)
                .build();

        Ingredient ingredient3 = Ingredient.builder()
                .name("Potato")
                .ingredientCategory(category1)
                .unitOfMeasure(UnitOfMeasure.GRAM)
                .cost(new BigDecimal(15))
                .status(ACTIVE)
                .build();

        Ingredient ingredient4 = Ingredient.builder()
                .name("Egg plant")
                .ingredientCategory(category1)
                .unitOfMeasure(UnitOfMeasure.GRAM)
                .cost(new BigDecimal(80))
                .status(ACTIVE)
                .build();

        List<Ingredient> ingredients = List.of(ingredient1, ingredient2, ingredient3, ingredient4);
        ingredientRepository.saveAll(ingredients);

        RecipeComposition composition1 = RecipeComposition.builder()
                .ingredient(ingredient1)
                .quantity(20.0)
                .build();

        RecipeComposition composition2 = RecipeComposition.builder()
                .ingredient(ingredient3)
                .quantity(300.0)
                .build();

        RecipeComposition composition3 = RecipeComposition.builder()
                .ingredient(ingredient2)
                .quantity(120.0)
                .build();

        RecipeComposition composition4 = RecipeComposition.builder()
                .ingredient(ingredient3)
                .quantity(175.0)
                .build();

        RecipeComposition composition5 = RecipeComposition.builder()
                .ingredient(ingredient4)
                .quantity(190.0)
                .build();

        Recipe recipe1 = Recipe.builder()
                .name("Potato pie")
                .description("Definitely the best pie ever")
                .recipeCategory(category3)
                .price(BigDecimal.valueOf(470))
                .cost(BigDecimal.valueOf(800 + 4500))
                .recipeCompositions(List.of(composition1, composition2))
                .location("kitchen")
                .build();

        Recipe recipe2 = Recipe.builder()
                .name("Ragout")
                .recipeCategory(category4)
                .price(BigDecimal.valueOf(750))
                .cost(BigDecimal.valueOf(4200 + 2625 + 15200))
                .recipeCompositions(List.of(composition3, composition4, composition5))
                .location("kitchen")
                .build();

        composition1.setRecipe(recipe1);
        composition2.setRecipe(recipe1);
        composition3.setRecipe(recipe2);
        composition4.setRecipe(recipe2);
        composition5.setRecipe(recipe2);
        recipeRepository.saveAll(List.of(recipe1, recipe2));
    }
}
