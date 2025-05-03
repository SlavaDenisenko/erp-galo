package com.denisenko.supplierservice.dto;

import com.denisenko.supplierservice.model.OrderStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupplierOrderDto {
    private Integer id;
    @NotNull(message = "Supplier ID is required")
    private Integer supplierId;
    private String supplierName;
    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItemDto> items;
    private OrderStatus status;
    private String supplierOrderId;
    private LocalDateTime createdAt;
}
