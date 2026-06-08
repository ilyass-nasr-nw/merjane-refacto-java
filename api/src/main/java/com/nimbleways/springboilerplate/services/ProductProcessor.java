package com.nimbleways.springboilerplate.services;

import com.nimbleways.springboilerplate.entities.Product;

public interface ProductProcessor {
    // Determines if this strategy applies to the product type
    boolean supports(String type);
    
    // Executes the specific business rules for the product
    void process(Product product);
}