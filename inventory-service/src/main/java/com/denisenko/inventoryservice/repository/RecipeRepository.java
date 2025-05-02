package com.denisenko.inventoryservice.repository;

import com.denisenko.inventoryservice.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface RecipeRepository extends JpaRepository<Recipe, Integer> {

    @Query("SELECT r.id, r.cost FROM Recipe r WHERE r.id IN :recipeIds")
    List<Object[]> getRecipeCostsNative(@Param("recipeIds") List<Integer> recipeIds);

    default Map<Integer, BigDecimal> getRecipeCosts(List<Integer> recipeIds) {
        List<Object[]> results = getRecipeCostsNative(recipeIds);
        Map<Integer, BigDecimal> recipeCosts = new HashMap<>();

        for (Object[] result : results) {
            Integer recipeId = (Integer) result[0];
            BigDecimal cost = (BigDecimal) result[1];
            recipeCosts.put(recipeId, cost);
        }

        return recipeCosts;
    }
}
