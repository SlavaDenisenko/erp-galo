package com.denisenko.supplierservice.exception;

public class OrderAlreadySentException extends RuntimeException {

    public OrderAlreadySentException(String message) {
        super(message);
    }

    public static OrderAlreadySentException forOrderId(Integer id) {
        return new OrderAlreadySentException(
                "Order with ID = " + id + " has already been sent and cannot be modified."
        );
    }
}
