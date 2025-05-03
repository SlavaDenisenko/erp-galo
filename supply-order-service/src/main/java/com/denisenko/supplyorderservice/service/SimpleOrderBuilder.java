package com.denisenko.supplyorderservice.service;

import com.denisenko.events.SupplyResponseEvent;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SimpleOrderBuilder implements OrderBuilder {

    @Override
    public SupplyResponseEvent buildOrder(String requestId, Map<String, Double> plannedOrder) {
        //- TODO here we can add validation

        return new SupplyResponseEvent(requestId, plannedOrder);
    }
}
