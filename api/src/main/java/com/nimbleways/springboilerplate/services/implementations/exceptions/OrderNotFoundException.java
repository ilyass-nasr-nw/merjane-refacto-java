package com.nimbleways.springboilerplate.services.implementations.exceptions;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long orderId) {
        super("Order Not Found: " + orderId);
    }
}
