package com.denisenko.model;

import java.time.LocalDate;
import java.util.List;

public class Supply {
    private Integer supplierId;
    private String supplierName;
    private String number;
    private List<SupplyItem> items;
    private LocalDate supplyDate;

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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public List<SupplyItem> getItems() {
        return items;
    }

    public void setItems(List<SupplyItem> items) {
        this.items = items;
    }

    public LocalDate getSupplyDate() {
        return supplyDate;
    }

    public void setSupplyDate(LocalDate supplyDate) {
        this.supplyDate = supplyDate;
    }

    @Override
    public String toString() {
        return "Supply{" +
                "supplierId=" + supplierId +
                ", supplierName='" + supplierName + '\'' +
                ", number='" + number + '\'' +
                ", items=" + items +
                ", supplyDate=" + supplyDate +
                '}';
    }
}
