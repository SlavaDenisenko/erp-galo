package com.denisenko.supplyorderservice.service;

import com.denisenko.events.SupplyResponseEvent;

import java.util.Map;

public interface OrderBuilder {
    SupplyResponseEvent buildOrder(String requestId, Map<String, Double> plannedOrder);
}
