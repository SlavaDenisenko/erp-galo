package com.denisenko.supplierservice.client;

import com.denisenko.supplierservice.dto.SupplierDto;
import com.denisenko.supplierservice.dto.SupplierOrderDto;
import com.denisenko.supplierservice.dto.SupplierProductDto;

import java.util.List;

public interface SupplierClient {
    List<SupplierProductDto> fetchProducts(SupplierDto supplierDto);

    String sendOrder(SupplierDto supplierDto, SupplierOrderDto supplierOrderDto);
}
