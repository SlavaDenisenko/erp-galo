package com.denisenko.supplierservice.mapper;

import com.denisenko.supplierservice.dto.OrderItemDto;
import com.denisenko.supplierservice.mapper.config.ConfigMapper;
import com.denisenko.supplierservice.mapper.config.DtoMapper;
import com.denisenko.supplierservice.mapper.config.EntityMapper;
import com.denisenko.supplierservice.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = ConfigMapper.class)
public interface OrderItemMapper extends DtoMapper<OrderItemDto, OrderItem>, EntityMapper<OrderItemDto, OrderItem> {

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    OrderItem toEntity(OrderItemDto orderItemDto);
}
