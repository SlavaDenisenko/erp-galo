package com.denisenko.orderservice.exception;

public class SagaException extends RuntimeException {

    public SagaException(String message) {
        super(message);
    }
}
