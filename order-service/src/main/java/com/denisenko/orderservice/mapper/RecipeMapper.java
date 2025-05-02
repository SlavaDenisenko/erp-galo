package com.denisenko.orderservice.mapper;

import com.denisenko.orderservice.dto.RecipeDto;
import com.denisenko.orderservice.mapper.config.ConfigMapper;
import com.denisenko.orderservice.mapper.config.DtoMapper;
import com.denisenko.orderservice.mapper.config.EntityMapper;
import com.denisenko.orderservice.model.Recipe;
import org.mapstruct.Mapper;

@Mapper(config = ConfigMapper.class)
public interface RecipeMapper extends DtoMapper<RecipeDto, Recipe>, EntityMapper<RecipeDto, Recipe> {
}
