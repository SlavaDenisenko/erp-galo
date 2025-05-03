package com.denisenko.supplierservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupplyDto {
    private Integer id;
    @NotNull(message = "Supplier ID is required")
    private Integer supplierId;
    @NotBlank(message = "Supplier name cannot be blank")
    private String supplierName;
    @NotBlank(message = "Document number is required")
    private String number;
    private List<SupplyItemDto> items;
    private String status;
    @NotNull(message = "Date cannot be null")
    private LocalDate supplyDate;
}
