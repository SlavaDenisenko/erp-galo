package com.denisenko.inventoryservice.mapper;

import com.denisenko.inventoryservice.dto.IngredientDto;
import com.denisenko.inventoryservice.mapper.config.ConfigMapper;
import com.denisenko.inventoryservice.mapper.config.DtoMapper;
import com.denisenko.inventoryservice.mapper.config.EntityMapper;
import com.denisenko.inventoryservice.model.Ingredient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = ConfigMapper.class, uses = {CategoryMapper.class})
public interface IngredientMapper extends DtoMapper<IngredientDto, Ingredient>, EntityMapper<IngredientDto, Ingredient> {

    @Override
    @Mapping(target = "categoryId", source = "ingredientCategory.id")
    @Mapping(target = "quantity", ignore = true)
    IngredientDto toDTO(Ingredient ingredient);

    @Override
    @Mapping(target = "ingredientCategory.id", source = "categoryId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Ingredient toEntity(IngredientDto ingredientDto);
}
