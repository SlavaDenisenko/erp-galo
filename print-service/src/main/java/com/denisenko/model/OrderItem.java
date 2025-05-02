package com.denisenko.model;

import java.math.BigDecimal;

public class OrderItem {
    private Integer itemId;
    private String itemName;
    private BigDecimal price;
    private Double quantity;
    private Integer course;
    private String location;
    private String reasonForDeletion;

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Integer getCourse() {
        return course;
    }

    public void setCourse(Integer course) {
        this.course = course;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getReasonForDeletion() {
        return reasonForDeletion;
    }

    public void setReasonForDeletion(String reasonForDeletion) {
        this.reasonForDeletion = reasonForDeletion;
    }
}
