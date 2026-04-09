package com.nimbleways.springboilerplate.services;

import com.nimbleways.springboilerplate.entities.Product;

@FunctionalInterface
public interface ProductHandler {
    void handle(Product product);
}
