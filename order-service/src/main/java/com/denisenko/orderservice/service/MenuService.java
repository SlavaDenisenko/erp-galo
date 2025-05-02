package com.denisenko.orderservice.service;

import com.denisenko.events.CategoryDeletedEvent;
import com.denisenko.events.CategoryUpdatedEvent;
import com.denisenko.events.RecipeDeletedEvent;
import com.denisenko.events.RecipeUpdatedEvent;
import com.denisenko.orderservice.client.InventoryClient;
import com.denisenko.orderservice.config.KafkaTopicsConfig;
import com.denisenko.orderservice.dto.CategoryDto;
import com.denisenko.orderservice.dto.MenuDto;
import com.denisenko.orderservice.dto.RecipeDto;
import com.denisenko.orderservice.mapper.CategoryEventMapper;
import com.denisenko.orderservice.mapper.CategoryMapper;
import com.denisenko.orderservice.mapper.RecipeEventMapper;
import com.denisenko.orderservice.mapper.RecipeMapper;
import com.denisenko.orderservice.model.Category;
import com.denisenko.orderservice.model.Recipe;
import com.denisenko.orderservice.repository.CategoryRepository;
import com.denisenko.orderservice.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuService {
    private final CategoryRepository categoryRepository;
    private final RecipeRepository recipeRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryEventMapper categoryEventMapper;
    private final RecipeMapper recipeMapper;
    private final RecipeEventMapper recipeEventMapper;
    private final KafkaTopicsConfig kafkaTopicsConfig;
    private final InventoryClient inventoryClient;

    public MenuDto getMenu() {
        List<Category> categories = toList(categoryRepository.findAll());
        return new MenuDto(buildCategoryTree(categories, null));
    }

    public List<RecipeDto> getRecipes(List<Integer> recipeIds) {
        List<Recipe> recipes = toList(recipeRepository.findAllById(recipeIds));
        return recipeMapper.toDTO(recipes);
    }

    @KafkaListener(topics = "#{kafkaTopicsConfig.recipeUpdatedTopic}", groupId = "${spring.kafka.consumer.group-id}")
    public void updateMenu(RecipeUpdatedEvent event) {
        log.info("An event was received about updating the recipe '{}' with ID = {}", event.getName(), event.getId());
        Recipe recipe = recipeEventMapper.toEntity(event);
        recipeRepository.save(recipe);
    }

    @KafkaListener(topics = "#{kafkaTopicsConfig.recipeDeletedTopic}", groupId = "${spring.kafka.consumer.group-id}")
    public void deleteRecipe(RecipeDeletedEvent event) {
        log.info("An event was received about deleting a recipe with ID = {} from the menu", event.getId());
        recipeRepository.deleteById(event.getId());
    }

    @KafkaListener(topics = "#{kafkaTopicsConfig.categoryUpdatedTopic}", groupId = "${spring.kafka.consumer.group-id}")
    public void updateMenu(CategoryUpdatedEvent event) {
        log.info("An event was received about updating the category '{}' with ID = {}", event.getName(), event.getId());
        Category category = categoryEventMapper.toEntity(event);
        categoryRepository.save(category);
    }

    @KafkaListener(topics = "#{kafkaTopicsConfig.categoryDeletedTopic}", groupId = "${spring.kafka.consumer.group-id}")
    public void deleteCategory(CategoryDeletedEvent event) {
        log.info("An event was received about deleting a category with ID = {} from the menu", event.getId());
        categoryRepository.deleteById(event.getId());
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeMenu() throws InterruptedException {
        log.info("Application started. Checking menu available in Redis...");
        MenuDto menu = getMenu();
        if (menu.getCategories() != null && !menu.getCategories().isEmpty()) {
            log.info("Menu found in Redis. Proceeding with application startup");
            return;
        }

        CategoryDto fullMenu = inventoryClient.getMenu("RECIPE");
        if (fullMenu.getChildren() == null || fullMenu.getChildren().isEmpty()) {
            Thread.sleep(30000L);
            fullMenu = inventoryClient.getMenu("RECIPE");
            if (fullMenu.getChildren() == null || fullMenu.getChildren().isEmpty()) {
                log.error("Failed to fetch menu from inventory service");
                return;
            }
        }


        List<Category> categories = new ArrayList<>();
        List<Recipe> recipes = new ArrayList<>();
        collectCategoriesAndRecipes(fullMenu, categories, recipes);

        categoryRepository.saveAll(categories);
        recipeRepository.saveAll(recipes);
        log.info("Menu successfully retrieved from Inventory Service and saved to Redis: {}", fullMenu);
    }

    private void collectCategoriesAndRecipes(CategoryDto categoryDto, List<Category> categories, List<Recipe> recipes) {
        if (categoryDto.getId() != null) {
            categories.add(categoryMapper.toEntity(categoryDto));
        }

        if (categoryDto.getChildren() != null) {
            for (CategoryDto category : categoryDto.getChildren()) {
                collectCategoriesAndRecipes(category, categories, recipes);
            }
        }

        if (categoryDto.getRecipes() != null) {
            for (RecipeDto recipe : categoryDto.getRecipes()) {
                recipes.add(recipeMapper.toEntity(recipe));
            }
        }
    }

    private List<CategoryDto> buildCategoryTree(List<Category> categories, Integer parentCategoryId) {
        List<CategoryDto> result = new ArrayList<>();
        for (Category category : categories) {
            if (Objects.equals(category.getParentCategoryId(), parentCategoryId)) {
                CategoryDto categoryDto = categoryMapper.toDTO(category);
                categoryDto.setChildren(buildCategoryTree(categories, category.getId()));
                List<Recipe> recipes = toList(recipeRepository.findByCategoryId(category.getId()));
                categoryDto.setRecipes(recipeMapper.toDTO(recipes));
                result.add(categoryDto);
            }
        }
        return result;
    }

    private <T> List<T> toList(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }
}
