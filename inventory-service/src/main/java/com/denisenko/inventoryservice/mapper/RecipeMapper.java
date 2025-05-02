package com.denisenko.inventoryservice.mapper;

import com.denisenko.inventoryservice.dto.RecipeDto;
import com.denisenko.inventoryservice.mapper.config.ConfigMapper;
import com.denisenko.inventoryservice.mapper.config.DtoMapper;
import com.denisenko.inventoryservice.mapper.config.EntityMapper;
import com.denisenko.inventoryservice.model.Recipe;
import com.denisenko.inventoryservice.model.RecipeCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = ConfigMapper.class, uses = {RecipeCompositionMapper.class})
public interface RecipeMapper extends DtoMapper<RecipeDto, Recipe>, EntityMapper<RecipeDto, Recipe> {

    @Override
    @Mapping(target = "categoryId", source = "recipeCategory.id")
    @Mapping(target = "compositions", source = "recipeCompositions")
    RecipeDto toDTO(Recipe recipe);

    @Override
    @Mapping(target = "recipeCategory.id", source = "categoryId")
    @Mapping(target = "recipeCompositions", source = "compositions")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Recipe toEntity(RecipeDto recipeDto);

    @Named("categoryIdToEntity")
    default RecipeCategory categoryIdToEntity(Integer id) {
        RecipeCategory category = new RecipeCategory();
        category.setId(id);
        return category;
    }
}
