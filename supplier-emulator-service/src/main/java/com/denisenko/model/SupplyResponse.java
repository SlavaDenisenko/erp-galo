package com.denisenko.model;

public record SupplyResponse(boolean success, String message) {

    public static SupplyResponse ok() {
        return new SupplyResponse(true, null);
    }

    public static SupplyResponse failed(String message) {
        return new SupplyResponse(false, message);
    }
}
