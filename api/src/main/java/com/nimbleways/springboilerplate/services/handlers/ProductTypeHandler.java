package com.nimbleways.springboilerplate.services.handlers;

import com.nimbleways.springboilerplate.entities.Product;

public interface ProductTypeHandler {
    void processProductOrder(Product product);
    String getSupportedType();
}
