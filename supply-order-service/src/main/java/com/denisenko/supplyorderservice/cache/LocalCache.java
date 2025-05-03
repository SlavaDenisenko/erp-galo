package com.denisenko.supplyorderservice.cache;

import com.denisenko.supplyorderservice.dto.SaleRecord;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LocalCache {
    private final Map<String, List<String>> requestsCache = new HashMap<>();
    private final Map<String, Map<String, Double>> inventoryCache = new HashMap<>();
    private final Map<String, Map<String, List<SaleRecord>>> salesCache = new HashMap<>();

    public void saveRequest(String requestId, List<String> systemProductIds) {
        requestsCache.put(requestId, systemProductIds);
    }

    public void saveInventoryData(String requestId, Map<String, Double> inventory) {
        inventoryCache.put(requestId, inventory);
    }

    public void saveSalesData(String requestId, Map<String, List<SaleRecord>> sales) {
        salesCache.put(requestId, sales);
    }

    public boolean isDataReady(String requestId) {
        return requestsCache.containsKey(requestId)
                && inventoryCache.containsKey(requestId);
        //&& salesCache.containsKey(requestId); TODO add sales
    }

    public List<String> getProducts(String requestId) {
        return requestsCache.get(requestId);
    }

    public Map<String, Double> getInventory(String requestId) {
        return inventoryCache.get(requestId);
    }

    public Map<String, List<SaleRecord>> getSales(String requestId) {
        return salesCache.get(requestId);
    }

    public void clear(String requestId) {
        requestsCache.remove(requestId);
        inventoryCache.remove(requestId);
        salesCache.remove(requestId);
    }
}
