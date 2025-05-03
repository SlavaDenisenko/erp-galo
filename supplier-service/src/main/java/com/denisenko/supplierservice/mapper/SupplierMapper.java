package com.denisenko.supplierservice.mapper;

import com.denisenko.supplierservice.dto.SupplierDto;
import com.denisenko.supplierservice.mapper.config.ConfigMapper;
import com.denisenko.supplierservice.mapper.config.DtoMapper;
import com.denisenko.supplierservice.mapper.config.EntityMapper;
import com.denisenko.supplierservice.model.Supplier;
import org.mapstruct.Mapper;

@Mapper(config = ConfigMapper.class, uses = {SupplierApiMethodMapper.class})
public interface SupplierMapper extends DtoMapper<SupplierDto, Supplier>, EntityMapper<SupplierDto, Supplier> {
}
