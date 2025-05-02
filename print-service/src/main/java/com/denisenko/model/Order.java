package com.denisenko.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private String ofdNumber;
    private Double tableNumber;
    private Integer numberOfGuests;
    private List<OrderItem> items;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
    private String waiterName;
    private BigDecimal totalPrice;
    private String paymentMethod;

    public String getOfdNumber() {
        return ofdNumber;
    }

    public void setOfdNumber(String ofdNumber) {
        this.ofdNumber = ofdNumber;
    }

    public Double getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(Double tableNumber) {
        this.tableNumber = tableNumber;
    }

    public Integer getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(Integer numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public String getWaiterName() {
        return waiterName;
    }

    public void setWaiterName(String waiterName) {
        this.waiterName = waiterName;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
