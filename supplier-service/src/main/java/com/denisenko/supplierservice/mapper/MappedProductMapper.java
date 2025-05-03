package com.denisenko.supplierservice.mapper;

import com.denisenko.supplierservice.dto.MappedProductDto;
import com.denisenko.supplierservice.mapper.config.ConfigMapper;
import com.denisenko.supplierservice.mapper.config.DtoMapper;
import com.denisenko.supplierservice.mapper.config.EntityMapper;
import com.denisenko.supplierservice.model.MappedProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = ConfigMapper.class, uses = {SupplierMapper.class})
public interface MappedProductMapper extends DtoMapper<MappedProductDto, MappedProduct>, EntityMapper<MappedProductDto, MappedProduct> {

    @Override
    @Mapping(target = "supplierId", source = "supplier.id")
    @Mapping(target = "inventoryProductId", source = "systemProductId")
    @Mapping(target = "inventoryProductName", source = "systemProductName")
    MappedProductDto toDTO(MappedProduct mappedProduct);

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "supplier.id", source = "supplierId")
    @Mapping(target = "systemProductId", source = "inventoryProductId")
    @Mapping(target = "systemProductName", source = "inventoryProductName")
    MappedProduct toEntity(MappedProductDto mappedProductDto);
}
