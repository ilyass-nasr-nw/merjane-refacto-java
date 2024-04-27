package com.nimbleways.springboilerplate.services;

import com.nimbleways.springboilerplate.entities.Product;

public interface ProductStrategy {
    Product processOrder(Product product);
}
