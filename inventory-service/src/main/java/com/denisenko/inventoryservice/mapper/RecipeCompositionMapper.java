package com.denisenko.inventoryservice.mapper;

import com.denisenko.inventoryservice.dto.RecipeCompositionDto;
import com.denisenko.inventoryservice.mapper.config.ConfigMapper;
import com.denisenko.inventoryservice.mapper.config.DtoMapper;
import com.denisenko.inventoryservice.mapper.config.EntityMapper;
import com.denisenko.inventoryservice.model.RecipeComposition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = ConfigMapper.class)
public interface RecipeCompositionMapper extends DtoMapper<RecipeCompositionDto, RecipeComposition>, EntityMapper<RecipeCompositionDto, RecipeComposition> {

    @Override
    @Mapping(target = "ingredientId", source = "ingredient.id")
    RecipeCompositionDto toDTO(RecipeComposition recipeComposition);

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recipe", ignore = true)
    @Mapping(target = "ingredient.id", source = "ingredientId")
    RecipeComposition toEntity(RecipeCompositionDto recipeCompositionDto);
}
