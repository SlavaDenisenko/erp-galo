package com.denisenko.supplierservice.dto;

import com.denisenko.supplierservice.model.ApiType;
import com.denisenko.supplierservice.model.SupplierStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupplierDto {
    private Integer id;
    @NotBlank(message = "Supplier name is required")
    @Size(min = 2, max = 100, message = "Supplier name must be between 2 and 100 characters")
    private String name;
    private String description;
    @Pattern(regexp = "\\+7\\s?\\(\\d{3}\\)\\s?\\d{3}-\\d{2}-\\d{2}", message = "Phone number must match the format +7 (xxx) xxx-xx-xx")
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    @NotNull(message = "Status is required")
    private SupplierStatus status;
    @NotEmpty(message = "Delivery days cannot be empty")
    private List<String> deliveryDays;
    private String apiUrl;
    private ApiType apiType;
    private List<SupplierApiMethodDto> apiMethods;
}
