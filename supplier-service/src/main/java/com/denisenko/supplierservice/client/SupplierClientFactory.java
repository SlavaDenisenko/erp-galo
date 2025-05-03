package com.denisenko.supplierservice.client;

import com.denisenko.supplierservice.dto.SupplierDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupplierClientFactory {
    private final RestSupplierClient restClient;
    private final SoapSupplierClient soapClient;

    public SupplierClient getClient(SupplierDto supplierDto) {
        return switch (supplierDto.getApiType()) {
            case REST -> restClient;
            case SOAP -> soapClient;
        };
    }
}
