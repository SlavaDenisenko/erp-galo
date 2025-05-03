package com.denisenko.supplierservice.mapper;

import com.denisenko.supplierservice.dto.SupplierOrderDto;
import com.denisenko.supplierservice.mapper.config.ConfigMapper;
import com.denisenko.supplierservice.mapper.config.DtoMapper;
import com.denisenko.supplierservice.mapper.config.EntityMapper;
import com.denisenko.supplierservice.model.SupplierOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = ConfigMapper.class, uses = {OrderItemMapper.class, SupplierMapper.class})
public interface SupplierOrderMapper extends DtoMapper<SupplierOrderDto, SupplierOrder>, EntityMapper<SupplierOrderDto, SupplierOrder> {

    @Override
    @Mapping(target = "supplierId", source = "supplier.id")
    @Mapping(target = "supplierName", ignore = true)
    SupplierOrderDto toDTO(SupplierOrder supplierOrder);

    @Override
    @Mapping(target = "supplier.id", source = "supplierId")
    SupplierOrder toEntity(SupplierOrderDto supplierOrderDto);
}
