package com.denisenko.orderservice.mapper;

import com.denisenko.orderservice.dto.OrderDto;
import com.denisenko.orderservice.mapper.config.ConfigMapper;
import com.denisenko.orderservice.mapper.config.DtoMapper;
import com.denisenko.orderservice.mapper.config.EntityMapper;
import com.denisenko.orderservice.model.Order;
import org.mapstruct.Mapper;

@Mapper(config = ConfigMapper.class, uses = {OrderItemMapper.class})
public interface OrderMapper extends DtoMapper<OrderDto, Order>, EntityMapper<OrderDto, Order> {
}
