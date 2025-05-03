package com.denisenko.supplierservice.exception;

public class PositionNotFoundException extends RuntimeException {

    public PositionNotFoundException(String message) {
        super(message);
    }
}
