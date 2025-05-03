package com.denisenko.supplyorderservice.service;

import com.denisenko.supplyorderservice.dto.ProductPlanningContext;

import java.util.List;
import java.util.Map;

public interface OrderPlanner {
    Map<String, Double> planOrder(List<ProductPlanningContext> contexts);

    String strategyName();
}
