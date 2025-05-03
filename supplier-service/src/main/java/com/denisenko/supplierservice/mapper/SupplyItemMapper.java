package com.denisenko.supplierservice.mapper;

import com.denisenko.supplierservice.dto.SupplyItemDto;
import com.denisenko.supplierservice.mapper.config.ConfigMapper;
import com.denisenko.supplierservice.mapper.config.DtoMapper;
import com.denisenko.supplierservice.mapper.config.EntityMapper;
import com.denisenko.supplierservice.model.SupplyItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = ConfigMapper.class)
public interface SupplyItemMapper extends DtoMapper<SupplyItemDto, SupplyItem>, EntityMapper<SupplyItemDto, SupplyItem> {

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "supply", ignore = true)
    SupplyItem toEntity(SupplyItemDto supplyItemDto);
}
