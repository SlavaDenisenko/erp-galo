package com.denisenko.inventoryservice.controller;

import com.denisenko.inventoryservice.dto.IngredientDto;
import com.denisenko.inventoryservice.service.IngredientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ingredients")
@RequiredArgsConstructor
@Validated
public class IngredientController {
    private final IngredientService ingredientService;

    @PostMapping
    public ResponseEntity<IngredientDto> createIngredient(@RequestBody @Valid IngredientDto ingredientDto,
                                                          @RequestHeader("Idempotency-Key") String idempotencyKey) {
        return new ResponseEntity<>(ingredientService.createIngredient(ingredientDto, idempotencyKey), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<IngredientDto>> getAllIngredients() {
        return new ResponseEntity<>(ingredientService.getAllIngredients(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredientDto> getIngredient(@PathVariable Integer id) {
        return new ResponseEntity<>(ingredientService.getIngredient(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredientDto> updateIngredient(@PathVariable Integer id, @RequestBody @Valid IngredientDto ingredientDto) {
        return new ResponseEntity<>(ingredientService.updateIngredient(id, ingredientDto), HttpStatus.OK);
    }
}
