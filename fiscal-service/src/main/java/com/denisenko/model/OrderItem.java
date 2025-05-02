package com.denisenko.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class OrderItem {
    private Integer itemId;
    private String itemName;
    private BigDecimal price;
    private Double quantity;
    private Integer course;
    private String status;
    private String reasonForDeletion;

    @Override
    public String toString() {
        return "OrderItem{" +
                "itemName='" + itemName + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", status='" + status + '\'' +
                '}';
    }
}
