package com.denisenko.supplierservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MappedProductDto {
    @NotNull(message = "Supplier ID is required")
    @Min(1)
    private Integer supplierId;
    @NotBlank(message = "Supplier product ID is required")
    private String supplierProductId;
    @NotBlank(message = "Supplier product name is required")
    private String supplierProductName;
    @NotNull(message = "System product ID is required")
    @Min(1)
    private Integer inventoryProductId;
    @NotBlank(message = "System product name is required")
    private String inventoryProductName;
}
