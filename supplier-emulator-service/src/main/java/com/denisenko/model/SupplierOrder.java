package com.denisenko.model;

import java.util.List;

public class SupplierOrder {
    private Integer supplierId;
    private String supplierName;
    private List<OrderItem> items;

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "SupplierOrder{" +
                "supplierId=" + supplierId +
                ", supplierName='" + supplierName + '\'' +
                ", items=" + items +
                '}';
    }
}
