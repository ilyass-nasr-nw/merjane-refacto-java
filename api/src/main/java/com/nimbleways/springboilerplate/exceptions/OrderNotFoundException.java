package com.nimbleways.springboilerplate.exceptions;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long orderId) {
        super("No order found with id " + orderId);
    }
}
