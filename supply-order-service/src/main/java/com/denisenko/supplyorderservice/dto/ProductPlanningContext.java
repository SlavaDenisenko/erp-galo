package com.denisenko.supplyorderservice.dto;

import java.util.List;

public record ProductPlanningContext(
        String productId,
        double currentStock,
        List<SaleRecord> recentSales
) {
}
