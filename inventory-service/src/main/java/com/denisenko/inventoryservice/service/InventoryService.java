package com.denisenko.inventoryservice.service;

import com.denisenko.inventoryservice.dto.IngredientDto;
import com.denisenko.inventoryservice.mapper.IngredientMapper;
import com.denisenko.inventoryservice.model.Ingredient;
import com.denisenko.inventoryservice.model.InventorySnapshot;
import com.denisenko.inventoryservice.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    private final RedisCacheService redisCacheService;
    private final IngredientRepository ingredientRepository;
    private final InventoryRepository inventoryRepository;
    private final InventorySnapshotRepository inventorySnapshotRepository;
    private final IngredientMapper ingredientMapper;

    private static final String IDEMPOTENCY_PREFIX = "idempotency:inventory:";

    public List<IngredientDto> getInventory(List<Integer> ingredientIds) {
        List<Ingredient> ingredients = ingredientRepository.findAllById(ingredientIds);
        Map<Integer, Double> stocks = inventoryRepository.findStocks(ingredientIds);
        return ingredients.stream()
                .map(ingredientMapper::toDTO)
                .peek(ingredientDto -> ingredientDto.setQuantity(stocks.getOrDefault(ingredientDto.getId(), 0.0)))
                .toList();
    }

    public List<IngredientDto> createSnapshot(List<Integer> ingredientIds, String idempotencyKey) {
        List<IngredientDto> exists = redisCacheService.get(IDEMPOTENCY_PREFIX + idempotencyKey, new TypeReference<>() {
        });
        if (exists != null) return exists;

        List<IngredientDto> inventory = getInventory(ingredientIds);
        List<InventorySnapshot> snapshots = new ArrayList<>();
        inventory.forEach(ingredient -> {
            InventorySnapshot snapshot = InventorySnapshot.builder()
                    .ingredient(ingredientMapper.toEntity(ingredient))
                    .stock(ingredient.getQuantity())
                    .build();
            snapshots.add(snapshot);
        });
        //- TODO remove last snapshots?
        inventorySnapshotRepository.saveAll(snapshots);
        redisCacheService.put(IDEMPOTENCY_PREFIX + idempotencyKey, inventory);
        return inventory;
    }
}
