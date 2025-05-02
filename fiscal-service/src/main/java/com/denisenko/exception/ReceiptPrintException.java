package com.denisenko.exception;

public class ReceiptPrintException extends RuntimeException {

    public ReceiptPrintException(String message) {
        super(message);
    }

    public ReceiptPrintException(String message, Throwable cause) {
        super(message, cause);
    }
}
