package com.denisenko.inventoryservice.repository;

import com.denisenko.inventoryservice.model.RecipeComposition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeCompositionRepository extends JpaRepository<RecipeComposition, Integer> {
}
