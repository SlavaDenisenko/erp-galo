package com.denisenko.supplyorderservice.service;

import com.denisenko.supplyorderservice.config.SupplyOrderStrategyProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OrderPlannerFactory {
    private final Map<String, OrderPlanner> planners;
    private final SupplyOrderStrategyProperties properties;

    public OrderPlannerFactory(List<OrderPlanner> planners, SupplyOrderStrategyProperties properties) {
        this.planners = planners.stream()
                .collect(Collectors.toMap(OrderPlanner::strategyName, Function.identity()));
        this.properties = properties;
    }

    public OrderPlanner getActivePlanner() {
        OrderPlanner planner = planners.get(properties.getPlannerStrategy());
        if (planner == null) {
            String availablePlanners = planners.values().stream().map(OrderPlanner::strategyName).collect(Collectors.joining(", "));
            throw new IllegalArgumentException("Unknown planner strategy: " + properties.getPlannerStrategy() + ". Available planners: " + availablePlanners);
        }
        return planner;
    }
}
