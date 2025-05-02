package com.denisenko.orderservice.mapper;

import com.denisenko.orderservice.dto.CategoryDto;
import com.denisenko.orderservice.mapper.config.ConfigMapper;
import com.denisenko.orderservice.mapper.config.DtoMapper;
import com.denisenko.orderservice.mapper.config.EntityMapper;
import com.denisenko.orderservice.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = ConfigMapper.class)
public interface CategoryMapper extends DtoMapper<CategoryDto, Category>, EntityMapper<CategoryDto, Category> {

    @Override
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "recipes", ignore = true)
    CategoryDto toDTO(Category category);
}
