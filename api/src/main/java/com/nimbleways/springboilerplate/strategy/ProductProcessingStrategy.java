package com.nimbleways.springboilerplate.strategy;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enums.ProductTypeEnum;

public interface ProductProcessingStrategy {
    ProductTypeEnum getType();
    void process(Product product);
}