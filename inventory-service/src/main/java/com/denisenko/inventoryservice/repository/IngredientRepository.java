package com.denisenko.inventoryservice.repository;

import com.denisenko.inventoryservice.model.Ingredient;
import com.denisenko.inventoryservice.model.IngredientStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {

    @Modifying
    @Query("""
            UPDATE Ingredient i SET i.cost = (
                SELECT m.cost FROM InventoryMovement m
                WHERE m.ingredient.id = i.id AND m.movementType = 'RECEIVED'
                ORDER BY m.movementDate DESC
                LIMIT 1
            )
            WHERE i.id IN :ingredientIds
            """)
    void updateIngredientPrices(@Param("ingredientIds") List<Integer> ingredientIds);

    @Query("SELECT i FROM Ingredient i WHERE i.status = :status")
    List<Ingredient> findAllByStatus(@Param("status") IngredientStatus status);
}
