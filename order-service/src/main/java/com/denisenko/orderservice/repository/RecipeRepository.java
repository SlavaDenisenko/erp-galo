package com.denisenko.orderservice.repository;

import com.denisenko.orderservice.model.Recipe;
import org.springframework.data.repository.CrudRepository;

public interface RecipeRepository extends CrudRepository<Recipe, Integer> {
    Iterable<Recipe> findByCategoryId(Integer categoryId);
}
