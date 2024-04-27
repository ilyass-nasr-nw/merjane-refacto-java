package com.nimbleways.springboilerplate.services;

import com.nimbleways.springboilerplate.entities.Product;

public interface ProductProcessor {
    Product processOrder(Product product);
}
