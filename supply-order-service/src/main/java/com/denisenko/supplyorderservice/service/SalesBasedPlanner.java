package com.denisenko.supplyorderservice.service;

import com.denisenko.supplyorderservice.dto.ProductPlanningContext;
import com.denisenko.supplyorderservice.dto.SaleRecord;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SalesBasedPlanner implements OrderPlanner {

    @Override
    public Map<String, Double> planOrder(List<ProductPlanningContext> contexts) {
        Map<String, Double> result = new HashMap<>();

        for (var context : contexts) {
            double averageDailySales = context.recentSales().stream()
                    .collect(Collectors.groupingBy(
                            sale -> sale.date().toLocalDate(),
                            Collectors.summingDouble(SaleRecord::quantity)))
                    .values().stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);

            double expectedConsumption = averageDailySales * 3;
            double toOrder = expectedConsumption - context.currentStock();

            if (toOrder > 0) {
                result.put(context.productId(), toOrder);
            }
        }

        return result;
    }

    @Override
    public String strategyName() {
        return "sales-based";
    }
}
