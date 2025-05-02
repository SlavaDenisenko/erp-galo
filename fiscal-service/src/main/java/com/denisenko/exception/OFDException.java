package com.denisenko.exception;

public class OFDException extends RuntimeException {

    public OFDException(String message) {
        super(message);
    }

    public OFDException(String message, Throwable cause) {
        super(message, cause);
    }
}
