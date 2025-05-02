package com.denisenko.inventoryservice.service;

import com.denisenko.events.CategoryDeletedEvent;
import com.denisenko.events.CategoryUpdatedEvent;
import com.denisenko.inventoryservice.dto.CategoryDto;
import com.denisenko.inventoryservice.exception.CategoryTypeModificationNotAllowedException;
import com.denisenko.inventoryservice.model.CategoryType;
import com.denisenko.inventoryservice.exception.PositionNotFoundException;
import com.denisenko.inventoryservice.mapper.CategoryMapper;
import com.denisenko.inventoryservice.model.Category;
import com.denisenko.inventoryservice.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final KafkaProducerService kafkaProducerService;
    private final StringRedisTemplate redisTemplate;

    private static final String IDEMPOTENCY_PREFIX = "idempotency:category:";

    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto, String idempotencyKey) {
        String categoryId = redisTemplate.opsForValue().get(IDEMPOTENCY_PREFIX + idempotencyKey);
        if (categoryId != null) {
            Category exists = categoryRepository.findById(Integer.parseInt(categoryId)).orElseThrow(() ->
                    new PositionNotFoundException("Category with ID = " + categoryId + " not found"));
            return categoryMapper.toDTO(exists);
        }

        Category category = categoryMapper.toEntity(categoryDto);
        categoryRepository.save(category);
        log.info("{} category '{}' is saved with ID = {}", category.getCategoryType(), category.getName(), category.getId());
        sendMessage(category.getId(), categoryDto);
        redisTemplate.opsForValue().set(IDEMPOTENCY_PREFIX + idempotencyKey, String.valueOf(category.getId()), Duration.ofMinutes(10));
        return categoryMapper.toDTO(category);
    }

    @Transactional(readOnly = true)
    public CategoryDto getAllCategories(CategoryType categoryType) {
        List<Category> categories = categoryRepository.findAllByCategoryType(categoryType);
        CategoryDto categoryDto = new CategoryDto();
        List<CategoryDto> children = new ArrayList<>();
        for (Category category : categories) {
            if (category.getParent() == null) {
                CategoryDto child = categoryMapper.toDTO(category);
                child.setChildren(getChildren(categories, category));
                children.add(child);
            }
        }
        categoryDto.setChildren(children);
        return categoryDto;
    }

    @Transactional
    public CategoryDto updateCategory(Integer id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new PositionNotFoundException("Category with ID = " + id + " not found"));
        if (category.getCategoryType() != categoryDto.getCategoryType()) {
            throw CategoryTypeModificationNotAllowedException.forCategoryId(category.getId());
        }
        Category updatedCategory = categoryMapper.toEntity(categoryDto);
        updatedCategory.setId(category.getId());
        categoryRepository.save(updatedCategory);
        sendMessage(id, categoryDto);
        return categoryMapper.toDTO(updatedCategory);
    }

    @Transactional
    public boolean deleteCategory(Integer id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new PositionNotFoundException("Category with ID = " + id + " not found"));
        if (category.hasContent()) return false;
        categoryRepository.delete(category);
        if (category.getCategoryType() == CategoryType.RECIPE) {
            kafkaProducerService.sendCategoryDeletedEvent(new CategoryDeletedEvent(id));
        }
        return true;
    }

    private void sendMessage(Integer id, CategoryDto categoryDto) {
        if (categoryDto.getCategoryType() == CategoryType.RECIPE) {
            categoryDto.setId(id);
            kafkaProducerService.sendCategoryUpdatedEvent(createCategoryUpdatedEvent(categoryDto));
        }
    }

    private List<CategoryDto> getChildren(List<Category> categories, Category parent) {
        List<CategoryDto> categoriesDto = new ArrayList<>();
        for (Category category : categories) {
            if (category.getParent() != null && category.getParent().getId().equals(parent.getId())) {
                CategoryDto categoryDto = categoryMapper.toDTO(category);
                categoryDto.setChildren(getChildren(categories, category));
                categoriesDto.add(categoryDto);
            }
        }
        return categoriesDto;
    }

    private CategoryUpdatedEvent createCategoryUpdatedEvent(CategoryDto categoryDto) {
        return CategoryUpdatedEvent.newBuilder()
                .setId(categoryDto.getId())
                .setName(categoryDto.getName())
                .setDescription(categoryDto.getDescription())
                .setParentCategoryId(categoryDto.getParentCategoryId())
                .build();
    }
}
