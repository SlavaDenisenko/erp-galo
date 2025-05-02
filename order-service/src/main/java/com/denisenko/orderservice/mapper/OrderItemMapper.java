package com.denisenko.orderservice.mapper;

import com.denisenko.orderservice.dto.OrderItemDto;
import com.denisenko.orderservice.mapper.config.ConfigMapper;
import com.denisenko.orderservice.mapper.config.DtoMapper;
import com.denisenko.orderservice.mapper.config.EntityMapper;
import com.denisenko.orderservice.model.OrderItem;
import org.mapstruct.Mapper;

@Mapper(config = ConfigMapper.class)
public interface OrderItemMapper extends DtoMapper<OrderItemDto, OrderItem>, EntityMapper<OrderItemDto, OrderItem> {
}
