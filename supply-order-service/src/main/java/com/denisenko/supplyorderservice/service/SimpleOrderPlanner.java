package com.denisenko.supplyorderservice.service;

import com.denisenko.supplyorderservice.dto.ProductPlanningContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SimpleOrderPlanner implements OrderPlanner {
    private static final double THRESHOLD = 5.0;
    private static final double TARGET = 20.0;

    @Override
    public Map<String, Double> planOrder(List<ProductPlanningContext> contexts) {
        Map<String, Double> result = new HashMap<>();

        for (var context : contexts) {
            if (context.currentStock() < THRESHOLD) {
                result.put(context.productId(), TARGET - context.currentStock());
            }
        }

        return result;
    }

    @Override
    public String strategyName() {
        return "threshold";
    }
}
