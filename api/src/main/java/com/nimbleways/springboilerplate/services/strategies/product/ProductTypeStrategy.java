package com.nimbleways.springboilerplate.services.strategies.product;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enums.ProductType;

/**
 * Strategy interface for processing products based on their type.
 */
public interface ProductTypeStrategy {

    /**
     * Processes the given product according to its type-specific rules.
     *
     * @param product The product to be processed.
     */
    void process(Product product);


    /**
     * Returns the product type associated with this strategy.
     *
     * @return The product type.
     */
    ProductType getProductType();

}
