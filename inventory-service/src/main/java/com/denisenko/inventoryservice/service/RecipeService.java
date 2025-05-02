package com.denisenko.inventoryservice.service;

import com.denisenko.events.RecipeDeletedEvent;
import com.denisenko.events.RecipeUpdatedEvent;
import com.denisenko.inventoryservice.dto.RecipeDto;
import com.denisenko.inventoryservice.exception.PositionNotFoundException;
import com.denisenko.inventoryservice.mapper.RecipeMapper;
import com.denisenko.inventoryservice.model.Recipe;
import com.denisenko.inventoryservice.model.RecipeComposition;
import com.denisenko.inventoryservice.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;
    private final IngredientService ingredientService;
    private final KafkaProducerService kafkaProducerService;
    private final StringRedisTemplate redisTemplate;

    private static final String IDEMPOTENCY_PREFIX = "idempotency:recipe:";

    @Transactional
    public RecipeDto createRecipe(RecipeDto recipeDto, String idempotencyKey) {
        String recipeId = redisTemplate.opsForValue().get(IDEMPOTENCY_PREFIX + idempotencyKey);
        if (recipeId != null) return getRecipe(Integer.parseInt(recipeId));

        Recipe recipe = recipeMapper.toEntity(recipeDto);
        recipe.setCost(calculateCost(recipe.getRecipeCompositions()));
        recipe.setRecipeCompositions(new ArrayList<>(recipe.getRecipeCompositions()));
        RecipeDto savedRecipe = recipeMapper.toDTO(recipeRepository.save(recipe));
        log.info("Recipe '{}' is saved with ID = {}", savedRecipe.getName(), savedRecipe.getId());
        redisTemplate.opsForValue().set(IDEMPOTENCY_PREFIX + idempotencyKey, String.valueOf(savedRecipe.getId()), Duration.ofMinutes(10));
        kafkaProducerService.sendRecipeUpdatedEvent(createRecipeUpdatedEvent(savedRecipe));
        return savedRecipe;
    }

    @Transactional
    public List<RecipeDto> getRecipes() {
        List<Recipe> recipes = recipeRepository.findAll();
        return recipeMapper.toDTO(recipes);
    }

    @Transactional
    public RecipeDto getRecipe(Integer id) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(() -> new PositionNotFoundException("Recipe with ID = " + id + " not found"));
        return recipeMapper.toDTO(recipe);
    }

    public Map<Integer, BigDecimal> getRecipeCosts(List<Integer> recipeIds) {
        return recipeRepository.getRecipeCosts(recipeIds);
    }

    @Transactional
    public RecipeDto updateRecipe(Integer id, RecipeDto recipeDto) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(() -> new PositionNotFoundException("Recipe with ID = " + id + " not found"));
        recipeDto.setId(id);
        Recipe updatedRecipe = recipeMapper.toEntity(recipeDto);
        updatedRecipe.setId(recipe.getId());
        updatedRecipe.setCost(calculateCost(updatedRecipe.getRecipeCompositions()));
        updatedRecipe.setRecipeCompositions(new ArrayList<>(updatedRecipe.getRecipeCompositions()));
        kafkaProducerService.sendRecipeUpdatedEvent(createRecipeUpdatedEvent(recipeDto));
        return recipeMapper.toDTO(recipeRepository.save(updatedRecipe));
    }

    @Transactional
    public void deleteRecipe(Integer id) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(() -> new PositionNotFoundException("Recipe with ID = " + id + " not found"));
        recipeRepository.delete(recipe);
        kafkaProducerService.sendRecipeDeletedEvent(new RecipeDeletedEvent(id));
    }

    private BigDecimal calculateCost(List<RecipeComposition> compositions) {
        BigDecimal totalCost = BigDecimal.ZERO;
        for (RecipeComposition composition : compositions) {
            //- FIXME fetch all ingredient costs
            BigDecimal positionCost = ingredientService.getIngredientCost(composition.getIngredient().getId());
            totalCost = totalCost.add(BigDecimal.valueOf(composition.getQuantity()).multiply(positionCost));
        }
        return totalCost;
    }

    private RecipeUpdatedEvent createRecipeUpdatedEvent(RecipeDto recipeDto) {
        return RecipeUpdatedEvent.newBuilder()
                .setId(recipeDto.getId())
                .setName(recipeDto.getName())
                .setDescription(recipeDto.getDescription())
                .setCategoryId(recipeDto.getCategoryId())
                .setPreparationTime(recipeDto.getPreparationTimeInMinutes())
                .setPrice(recipeDto.getPrice())
                .setInstructions(recipeDto.getInstructions())
                .setLocation(recipeDto.getLocation())
                .build();
    }
}
