package com.denisenko.orderservice.mapper;

import com.denisenko.events.RecipeUpdatedEvent;
import com.denisenko.orderservice.mapper.config.ConfigMapper;
import com.denisenko.orderservice.mapper.config.EntityMapper;
import com.denisenko.orderservice.model.Recipe;
import org.mapstruct.Mapper;

@Mapper(config = ConfigMapper.class, uses = DateTimeMapper.class)
public interface RecipeEventMapper extends EntityMapper<RecipeUpdatedEvent, Recipe> {
}
