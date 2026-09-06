package com.nimbleways.springboilerplate.services.implementations.strategies;

import com.nimbleways.springboilerplate.entities.Product;

public interface ProductStrategy {

    String getProductType();

    void handleOrder(Product product);
}
