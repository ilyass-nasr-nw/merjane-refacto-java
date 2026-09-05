package com.nimbleways.springboilerplate.services.products;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.ProductType;

/** Stock and notification rules of one product family. */
public interface ProductProcessor {

    ProductType supportedType();

    void process(Product product);
}
