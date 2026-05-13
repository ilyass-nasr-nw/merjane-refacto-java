package com.nimbleways.springboilerplate.services.implementations.availability;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.ProductType;

public interface ProductAvailabilityStrategy {

    boolean supports(ProductType productType);

    void process(Product product);
}
