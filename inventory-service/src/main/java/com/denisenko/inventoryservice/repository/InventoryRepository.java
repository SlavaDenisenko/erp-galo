package com.denisenko.inventoryservice.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class InventoryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Map<Integer, Double> findStocks(List<Integer> ingredientIds) {
        if (ingredientIds == null || ingredientIds.isEmpty()) {
            return Collections.emptyMap();
        }

        String jpql = """
                SELECT i.id,
                        COALESCE(s.stock, 0) +
                        COALESCE(SUM(CASE
                            WHEN m.movementType = 'RECEIVED' THEN m.quantity
                            WHEN m.movementType = 'DEDUCTED' THEN -m.quantity
                            WHEN m.movementType = 'ADJUSTMENT_INCREASE' THEN m.quantity
                            WHEN m.movementType = 'ADJUSTMENT_DECREASE' THEN -m.quantity
                            ELSE 0
                        END), 0)
                FROM Ingredient i
                LEFT JOIN InventorySnapshot s ON i.id = s.ingredient.id
                LEFT JOIN InventoryMovement m ON i.id = m.ingredient.id
                    AND m.createdAt > COALESCE(s.snapshotDate, '1970-01-01')
                WHERE i.id IN :ingredientIds
                GROUP BY i.id, s.stock
                """;

        List<Object[]> result = entityManager.createQuery(jpql, Object[].class)
                .setParameter("ingredientIds", ingredientIds)
                .getResultList();

        return result.stream()
                .collect(Collectors.toMap(
                        row -> (Integer) row[0],
                        row -> (Double) row[1]
                ));
    }
}
