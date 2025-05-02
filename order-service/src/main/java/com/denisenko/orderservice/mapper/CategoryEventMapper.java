package com.denisenko.orderservice.mapper;

import com.denisenko.events.CategoryUpdatedEvent;
import com.denisenko.orderservice.mapper.config.ConfigMapper;
import com.denisenko.orderservice.mapper.config.EntityMapper;
import com.denisenko.orderservice.model.Category;
import org.mapstruct.Mapper;

@Mapper(config = ConfigMapper.class, uses = DateTimeMapper.class)
public interface CategoryEventMapper extends EntityMapper<CategoryUpdatedEvent, Category> {
}
