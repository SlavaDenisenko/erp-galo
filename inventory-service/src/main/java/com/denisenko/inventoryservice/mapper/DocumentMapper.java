package com.denisenko.inventoryservice.mapper;

import com.denisenko.inventoryservice.dto.DocumentDto;
import com.denisenko.inventoryservice.mapper.config.ConfigMapper;
import com.denisenko.inventoryservice.mapper.config.DtoMapper;
import com.denisenko.inventoryservice.mapper.config.EntityMapper;
import com.denisenko.inventoryservice.model.Document;
import org.mapstruct.Mapper;

@Mapper(config = ConfigMapper.class, uses = {InventoryMovementMapper.class})
public interface DocumentMapper extends DtoMapper<DocumentDto, Document>, EntityMapper<DocumentDto, Document> {
}
