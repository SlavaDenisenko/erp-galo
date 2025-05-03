package com.denisenko.supplyorderservice.service;

import com.denisenko.events.InventoryRequestEvent;
import com.denisenko.events.InventoryResponseEvent;
import com.denisenko.events.SupplyRequestEvent;
import com.denisenko.events.SupplyResponseEvent;
import com.denisenko.supplyorderservice.cache.LocalCache;
import com.denisenko.supplyorderservice.dto.ProductPlanningContext;
import com.denisenko.supplyorderservice.dto.SaleRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupplyOrderService {
    private final OrderPlannerFactory orderPlannerFactory;
    private final OrderBuilder orderBuilder;
    private final KafkaProducerService kafkaProducerService;
    private final LocalCache localCache;

    public void handleSupplyRequest(SupplyRequestEvent event) {
        localCache.saveRequest(event.getRequestId(), event.getSystemProductIds());
        kafkaProducerService.sendInventoryRequestEvent(new InventoryRequestEvent(event.getRequestId(), event.getSystemProductIds()));
    }

    public void handleInventoryResponse(InventoryResponseEvent event) {
        localCache.saveInventoryData(event.getRequestId(), event.getInventory());
        tryToBuildOrder(event.getRequestId());
    }

    private void tryToBuildOrder(String requestId) {
        if (!localCache.isDataReady(requestId)) {
            log.warn("The information needed to create a forecast has not yet been collected for requestId = {}", requestId);
            return;
        }

        OrderPlanner orderPlanner = orderPlannerFactory.getActivePlanner();
        Map<String, Double> plannedOrder = orderPlanner.planOrder(buildContexts(
                localCache.getProducts(requestId),
                localCache.getInventory(requestId),
                localCache.getSales(requestId)
        ));


        SupplyResponseEvent event = orderBuilder.buildOrder(requestId, plannedOrder);
        kafkaProducerService.sendSupplyResponseEvent(event);
    }

    private List<ProductPlanningContext> buildContexts(List<String> systemProductIds, Map<String, Double> stocks, Map<String, List<SaleRecord>> sales) {
        return systemProductIds.stream()
                .map(id -> new ProductPlanningContext(
                        id,
                        stocks.getOrDefault(id, 0.0),
                        Optional.ofNullable(sales).orElse(Map.of()).getOrDefault(id, List.of())
                ))
                .toList();
    }
}
