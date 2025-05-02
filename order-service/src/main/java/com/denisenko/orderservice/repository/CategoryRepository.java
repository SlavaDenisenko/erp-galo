package com.denisenko.orderservice.repository;

import com.denisenko.orderservice.model.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Integer> {
}
