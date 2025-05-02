package com.denisenko.inventoryservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentDto {
    private Integer id;
    @NotBlank(message = "Document number cannot be blank")
    private String documentNumber;
    @NotNull(message = "Document date cannot be null")
    private LocalDate documentDate;
    @NotBlank(message = "Document type cannot be blank")
    private String documentType;
    @NotBlank(message = "Supplier name cannot be blank")
    private String supplierName;
    private List<InventoryMovementDto> inventoryMovements;
}
