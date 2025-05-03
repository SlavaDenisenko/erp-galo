package com.denisenko.supplierservice.mapper;

import com.denisenko.supplierservice.dto.SupplierApiMethodDto;
import com.denisenko.supplierservice.mapper.config.ConfigMapper;
import com.denisenko.supplierservice.mapper.config.DtoMapper;
import com.denisenko.supplierservice.mapper.config.EntityMapper;
import com.denisenko.supplierservice.model.SupplierApiMethod;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = ConfigMapper.class)
public interface SupplierApiMethodMapper extends DtoMapper<SupplierApiMethodDto, SupplierApiMethod>, EntityMapper<SupplierApiMethodDto, SupplierApiMethod> {

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    SupplierApiMethod toEntity(SupplierApiMethodDto supplierApiMethodDto);
}
