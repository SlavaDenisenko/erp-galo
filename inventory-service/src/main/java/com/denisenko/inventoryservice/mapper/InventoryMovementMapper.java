package com.denisenko.inventoryservice.mapper;

import com.denisenko.inventoryservice.dto.InventoryMovementDto;
import com.denisenko.inventoryservice.mapper.config.ConfigMapper;
import com.denisenko.inventoryservice.mapper.config.DtoMapper;
import com.denisenko.inventoryservice.mapper.config.EntityMapper;
import com.denisenko.inventoryservice.model.InventoryMovement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = ConfigMapper.class)
public interface InventoryMovementMapper extends DtoMapper<InventoryMovementDto, InventoryMovement>, EntityMapper<InventoryMovementDto, InventoryMovement> {

    @Override
    @Mapping(target = "ingredientId", source = "ingredient.id")
    @Mapping(target = "associatedDocumentId", source = "associatedDocument.id")
    InventoryMovementDto toDTO(InventoryMovement inventoryMovement);

    @Override
    @Mapping(target = "ingredient.id", source = "ingredientId")
    @Mapping(target = "associatedDocument.id", source = "associatedDocumentId")
    @Mapping(target = "createdAt", ignore = true)
    InventoryMovement toEntity(InventoryMovementDto inventoryMovementDto);
}
