package com.denisenko.supplierservice.client;

import com.denisenko.supplierservice.dto.SupplierApiMethodDto;
import com.denisenko.supplierservice.dto.SupplierDto;
import com.denisenko.supplierservice.dto.SupplierOrderDto;
import com.denisenko.supplierservice.dto.SupplierProductDto;
import com.denisenko.supplierservice.exception.SupplierCommunicationException;
import com.denisenko.supplierservice.model.ApiAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestSupplierClient implements SupplierClient {
    private final RestTemplate restTemplate;

    @Override
    public List<SupplierProductDto> fetchProducts(SupplierDto supplierDto) {
        SupplierApiMethodDto apiMethod = getApiMethod(supplierDto, ApiAction.FETCH_PRODUCTS);
        String url = supplierDto.getApiUrl() + apiMethod.getPath();

        ResponseEntity<List<SupplierProductDto>> response = restTemplate.exchange(
                url,
                HttpMethod.valueOf(apiMethod.getHttpMethod().name()),
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        return response.getBody();
    }

    @Override
    public String sendOrder(SupplierDto supplierDto, SupplierOrderDto supplierOrderDto) {
        SupplierApiMethodDto apiMethod = getApiMethod(supplierDto, ApiAction.SEND_ORDER);
        String url = supplierDto.getApiUrl() + apiMethod.getPath();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<SupplierOrderDto> requestEntity = new HttpEntity<>(supplierOrderDto, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.valueOf(apiMethod.getHttpMethod().name()),
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                String orderNumber = response.getBody();
                log.info("Order successfully sent to supplier '{}' with order number: {}", supplierDto.getName(), orderNumber);
                return orderNumber;
            } else {
                log.error("Failed to send order to supplier '{}'. HTTP Status: {}", supplierDto.getName(), response.getStatusCode());
                throw new SupplierCommunicationException("Failed to send order to supplier");
            }
        } catch (Exception e) {
            log.error("Error while sending order to supplier '{}': {}", supplierDto.getName(), e.getMessage());
            throw new SupplierCommunicationException("Error while sending order to supplier", e);
        }
    }

    private SupplierApiMethodDto getApiMethod(SupplierDto supplierDto, ApiAction apiAction) {
        return supplierDto.getApiMethods().stream()
                .filter(apiMethod -> apiAction == apiMethod.getActionName())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("API method '" + apiAction.name() + "' not defined for supplier '" + supplierDto.getName() + "'"));
    }
}
