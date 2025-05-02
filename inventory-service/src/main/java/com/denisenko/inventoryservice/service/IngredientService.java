package com.denisenko.inventoryservice.service;

import com.denisenko.inventoryservice.dto.IngredientDto;
import com.denisenko.inventoryservice.exception.PositionNotFoundException;
import com.denisenko.inventoryservice.mapper.IngredientMapper;
import com.denisenko.inventoryservice.model.Ingredient;
import com.denisenko.inventoryservice.model.IngredientCategory;
import com.denisenko.inventoryservice.model.InventoryMovement;
import com.denisenko.inventoryservice.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import static com.denisenko.inventoryservice.model.IngredientStatus.ACTIVE;
import static com.denisenko.inventoryservice.model.IngredientStatus.DELETED;

@Service
@RequiredArgsConstructor
@Slf4j
public class IngredientService {
    private final IngredientRepository ingredientRepository;
    private final IngredientMapper ingredientMapper;
    private final StringRedisTemplate redisTemplate;

    private static final String IDEMPOTENCY_PREFIX = "idempotency:ingredient:";

    public IngredientDto createIngredient(IngredientDto ingredientDto, String idempotencyKey) {
        String ingredientId = redisTemplate.opsForValue().get(IDEMPOTENCY_PREFIX + idempotencyKey);
        if (ingredientId != null) return getIngredient(Integer.parseInt(ingredientId));

        Ingredient ingredient = ingredientMapper.toEntity(ingredientDto);
        ingredient.setCost(BigDecimal.ZERO);
        ingredient.setStatus(ACTIVE);
        ingredientRepository.save(ingredient);
        redisTemplate.opsForValue().set(IDEMPOTENCY_PREFIX + idempotencyKey, String.valueOf(ingredient.getId()), Duration.ofMinutes(10));
        log.info("Ingredient '{}' is saved with ID = {}", ingredient.getName(), ingredient.getId());
        return ingredientMapper.toDTO(ingredient);
    }

    public List<IngredientDto> getAllIngredients() {
        List<Ingredient> ingredients = ingredientRepository.findAll();
        return ingredientMapper.toDTO(ingredients);
    }

    public List<IngredientDto> getAllActiveIngredients() {
        List<Ingredient> activeIngredients = ingredientRepository.findAllByStatus(ACTIVE);
        return ingredientMapper.toDTO(activeIngredients);
    }

    public List<IngredientDto> getAllDeletedIngredients() {
        List<Ingredient> deletedIngredients = ingredientRepository.findAllByStatus(DELETED);
        return ingredientMapper.toDTO(deletedIngredients);
    }

    public List<IngredientDto> getIngredients(List<Integer> ids) {
        List<Ingredient> ingredients = ingredientRepository.findAllById(ids);
        return ingredientMapper.toDTO(ingredients);
    }

    public IngredientDto getIngredient(Integer id) {
        Ingredient ingredient = ingredientRepository.findById(id).orElseThrow(() -> new PositionNotFoundException("Ingredient with ID = " + id + " not found"));
        return ingredientMapper.toDTO(ingredient);
    }

    @Transactional
    public IngredientDto updateIngredient(Integer id, IngredientDto ingredientDto) {
        Ingredient ingredient = ingredientRepository.findById(id).orElseThrow(() -> new PositionNotFoundException("Ingredient with ID = " + id + " not found"));
        ingredient.setName(ingredientDto.getName());
        IngredientCategory category = new IngredientCategory();
        category.setId(ingredientDto.getCategoryId());
        ingredient.setIngredientCategory(category);
        ingredient.setReorderLevel(ingredientDto.getReorderLevel());
        return ingredientMapper.toDTO(ingredientRepository.save(ingredient));
    }

    public void updateIngredientPrices(List<InventoryMovement> inventoryMovements) {
        List<Integer> ingredientIds = inventoryMovements.stream()
                .map(m -> m.getIngredient().getId())
                .distinct()
                .toList();

        ingredientRepository.updateIngredientPrices(ingredientIds);
    }

    public BigDecimal getIngredientCost(Integer id) {
        Ingredient ingredient = ingredientRepository.findById(id).orElseThrow(() -> new PositionNotFoundException("Ingredient with ID = " + id + " not found"));
        return ingredient.getCost();
    }
}
