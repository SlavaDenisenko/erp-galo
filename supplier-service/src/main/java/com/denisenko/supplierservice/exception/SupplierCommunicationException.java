package com.denisenko.supplierservice.exception;

public class SupplierCommunicationException extends RuntimeException {

    public SupplierCommunicationException(String message) {
        super(message);
    }

    public SupplierCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
