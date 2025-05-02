package com.denisenko.reportservice.mapper;

import com.denisenko.events.OrderClosedEvent;
import com.denisenko.reportservice.mapper.config.ConfigMapper;
import com.denisenko.reportservice.mapper.config.EntityMapper;
import com.denisenko.reportservice.model.Sale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = ConfigMapper.class, uses = {DateTimeMapper.class, SaleItemMapper.class})
public interface SaleMapper extends EntityMapper<OrderClosedEvent, Sale> {

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "shift", ignore = true)
    Sale toEntity(OrderClosedEvent orderClosedEvent);
}
