package com.denisenko.supplierservice.dto;

import com.denisenko.supplierservice.model.ApiAction;
import com.denisenko.supplierservice.model.HttpMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupplierApiMethodDto {
    private ApiAction actionName;
    private HttpMethod httpMethod;
    private String path;
}
