package com.denisenko.inventoryservice.controller;

import com.denisenko.inventoryservice.dto.IngredientDto;
import com.denisenko.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<List<IngredientDto>> getInventory(@RequestBody List<Integer> ingredientIds) {
        return ResponseEntity.ok(inventoryService.getInventory(ingredientIds));
    }

    @PostMapping("/snapshot")
    public ResponseEntity<List<IngredientDto>> createSnapshot(@RequestBody List<Integer> ingredientIds,
                                                              @RequestHeader("Idempotency-Key") String idempotencyKey) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(inventoryService.createSnapshot(ingredientIds, idempotencyKey));
    }
}
