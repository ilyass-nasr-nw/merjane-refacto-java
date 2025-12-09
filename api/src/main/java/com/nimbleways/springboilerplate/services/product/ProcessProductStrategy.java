package com.nimbleways.springboilerplate.services.product;

import com.nimbleways.springboilerplate.entities.product.Product;

public interface ProcessProductStrategy {
    Product processProduct(Product product);

    default void handleAvailability(Product product) {
        product.setAvailable(product.getAvailable() - 1);
    }
}
