package com.denisenko.service;

import com.denisenko.client.SupplierClient;
import com.denisenko.model.SupplierOrder;
import com.denisenko.model.Supply;
import com.denisenko.model.SupplyItem;
import com.denisenko.model.SupplyResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@ApplicationScoped
public class SupplyService {

    @Inject
    SupplierClient supplierClient;

    public SupplyResponse sendSupply(SupplierOrder order) {
        Supply supply = prepareSupply(order);
        return supplierClient.sendSupply(supply);
    }

    private Supply prepareSupply(SupplierOrder order) {
        List<SupplyItem> items = order.getItems().stream()
                .map(i -> {
                    SupplyItem item = new SupplyItem();
                    item.setProductId(i.getProductId());
                    item.setProductName(i.getProductName());
                    item.setQuantity(i.getQuantity());
                    item.setCost(new BigDecimal(ThreadLocalRandom.current().nextInt(100, 1001)));
                    item.setUnitOfMeasure(i.getUnitOfMeasure());
                    return item;
                })
                .toList();

        Supply supply = new Supply();
        supply.setSupplierId(order.getSupplierId());
        supply.setSupplierName(order.getSupplierName());
        supply.setNumber(UUID.randomUUID().toString());
        supply.setItems(items);
        supply.setSupplyDate(LocalDate.now());
        return supply;
    }
}
