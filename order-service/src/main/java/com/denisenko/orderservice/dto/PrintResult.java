package com.denisenko.orderservice.dto;

public record PrintResult(boolean success, String message) {

    public static PrintResult ok() {
        return new PrintResult(true, null);
    }

    public static PrintResult failed(String message) {
        return new PrintResult(false, message);
    }
}
