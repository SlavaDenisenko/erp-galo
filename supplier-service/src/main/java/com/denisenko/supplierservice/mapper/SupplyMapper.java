package com.denisenko.supplierservice.mapper;

import com.denisenko.supplierservice.dto.SupplyDto;
import com.denisenko.supplierservice.mapper.config.ConfigMapper;
import com.denisenko.supplierservice.mapper.config.DtoMapper;
import com.denisenko.supplierservice.mapper.config.EntityMapper;
import com.denisenko.supplierservice.model.Supply;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = ConfigMapper.class, uses = {SupplyItemMapper.class, SupplierMapper.class})
public interface SupplyMapper extends DtoMapper<SupplyDto, Supply>, EntityMapper<SupplyDto, Supply> {

    @Override
    @Mapping(target = "supplierId", source = "supplier.id")
    @Mapping(target = "supplierName", source = "supplier.name")
    SupplyDto toDTO(Supply supply);

    @Override
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "supplier.id", source = "supplierId")
    Supply toEntity(SupplyDto supplyDto);
}
