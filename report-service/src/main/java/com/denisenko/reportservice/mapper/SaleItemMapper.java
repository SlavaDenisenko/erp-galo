package com.denisenko.reportservice.mapper;

import com.denisenko.events.OrderClosedItem;
import com.denisenko.reportservice.mapper.config.ConfigMapper;
import com.denisenko.reportservice.mapper.config.EntityMapper;
import com.denisenko.reportservice.model.SaleItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = ConfigMapper.class)
public interface SaleItemMapper extends EntityMapper<OrderClosedItem, SaleItem> {

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sale", ignore = true)
    SaleItem toEntity(OrderClosedItem orderClosedItem);
}
