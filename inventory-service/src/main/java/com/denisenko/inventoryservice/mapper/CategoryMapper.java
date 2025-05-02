package com.denisenko.inventoryservice.mapper;

import com.denisenko.inventoryservice.dto.CategoryDto;
import com.denisenko.inventoryservice.mapper.config.ConfigMapper;
import com.denisenko.inventoryservice.mapper.config.DtoMapper;
import com.denisenko.inventoryservice.mapper.config.EntityMapper;
import com.denisenko.inventoryservice.model.Category;
import com.denisenko.inventoryservice.model.IngredientCategory;
import com.denisenko.inventoryservice.model.RecipeCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = ConfigMapper.class, uses = {IngredientMapper.class, RecipeMapper.class})
public interface CategoryMapper extends DtoMapper<CategoryDto, Category>, EntityMapper<CategoryDto, Category> {

    @Override
    default CategoryDto toDTO(Category category) {
        return switch (category.getCategoryType()) {
            case INGREDIENT -> toDTO((IngredientCategory) category);
            case RECIPE -> toDTO((RecipeCategory) category);
        };
    }

    @Override
    default Category toEntity(CategoryDto categoryDto) {
        return switch (categoryDto.getCategoryType()) {
            case INGREDIENT -> toIngredientCategoryEntity(categoryDto);
            case RECIPE -> toRecipeCategoryEntity(categoryDto);
        };
    }

    @Mapping(target = "parentCategoryId", source = "parent.id")
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "recipes", ignore = true)
    CategoryDto toDTO(IngredientCategory category);

    @Mapping(target = "parentCategoryId", source = "parent.id")
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "ingredients", ignore = true)
    CategoryDto toDTO(RecipeCategory category);

    @Mapping(target = "parent", source = "parentCategoryId", qualifiedByName = "mapIngredientParent")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "ingredients", ignore = true)
    IngredientCategory toIngredientCategoryEntity(CategoryDto categoryDto);

    @Mapping(target = "parent", source = "parentCategoryId", qualifiedByName = "mapRecipeCategory")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "recipes", ignore = true)
    RecipeCategory toRecipeCategoryEntity(CategoryDto categoryDto);

    @Named("mapIngredientParent")
    default IngredientCategory mapIngredientParent(Integer parentCategoryId) {
        if (parentCategoryId == null) return null;
        IngredientCategory parent = new IngredientCategory();
        parent.setId(parentCategoryId);
        return parent;
    }

    @Named("mapRecipeCategory")
    default RecipeCategory mapRecipeCategory(Integer parentCategoryId) {
        if (parentCategoryId == null) return null;
        RecipeCategory parent = new RecipeCategory();
        parent.setId(parentCategoryId);
        return parent;
    }
}
