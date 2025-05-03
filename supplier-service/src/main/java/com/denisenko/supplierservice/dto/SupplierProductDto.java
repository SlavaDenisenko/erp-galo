package com.denisenko.supplierservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupplierProductDto {
    private String productId;
    private String name;
    private Double price;
}
