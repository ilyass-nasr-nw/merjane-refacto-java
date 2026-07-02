package com.nimbleways.springboilerplate.services.processing;

import com.nimbleways.springboilerplate.entities.Product;

public interface ProductProcessingStrategy {

    boolean supports(Product product);

    void process(Product product);
}
