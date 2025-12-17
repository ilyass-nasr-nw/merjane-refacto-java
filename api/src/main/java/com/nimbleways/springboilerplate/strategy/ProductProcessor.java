package com.nimbleways.springboilerplate.strategy;

import com.nimbleways.springboilerplate.entities.Product;

public interface ProductProcessor {
    void process(Product product);
}
