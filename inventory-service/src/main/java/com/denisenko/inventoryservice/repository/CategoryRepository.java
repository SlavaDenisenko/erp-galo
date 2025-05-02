package com.denisenko.inventoryservice.repository;

import com.denisenko.inventoryservice.model.CategoryType;
import com.denisenko.inventoryservice.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findAllByCategoryType(CategoryType categoryType);
}
