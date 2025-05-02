package com.denisenko.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class Order {
    private Double tableNumber;
    private Integer numberOfGuests;
    private List<OrderItem> items;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;
    private BigDecimal totalPrice;
    private String waiterName;
    private String paymentMethod;
    private String ofdNumber;

    @Override
    public String toString() {
        return "Order{" +
                "tableNumber=" + tableNumber +
                ", totalPrice=" + totalPrice +
                ", waiterName='" + waiterName + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", items=" + items +
                '}';
    }
}
