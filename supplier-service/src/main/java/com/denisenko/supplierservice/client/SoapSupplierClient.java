package com.denisenko.supplierservice.client;

import com.denisenko.supplierservice.dto.SupplierDto;
import com.denisenko.supplierservice.dto.SupplierOrderDto;
import com.denisenko.supplierservice.dto.SupplierProductDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SoapSupplierClient implements SupplierClient {

    @Override
    public List<SupplierProductDto> fetchProducts(SupplierDto supplierDto) {
        throw new RuntimeException("SOAP client not realized yet");
    }

    @Override
    public String sendOrder(SupplierDto supplierDto, SupplierOrderDto supplierOrderDto) {
        throw new RuntimeException("SOAP client not realized yet");
    }
}
