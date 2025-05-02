package com.denisenko.exception;

public class RedisStorageException extends RuntimeException {

    public RedisStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
