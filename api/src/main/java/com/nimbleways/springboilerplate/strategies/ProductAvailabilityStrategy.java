package com.nimbleways.springboilerplate.strategies;

import com.nimbleways.springboilerplate.entities.Product;

public interface ProductAvailabilityStrategy {

    void processOrder(Product product);
}
