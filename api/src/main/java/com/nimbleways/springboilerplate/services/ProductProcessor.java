package com.nimbleways.springboilerplate.services;

import com.nimbleways.springboilerplate.entities.Product;

/**
 * Strategy interface responsible for applying inventory rules on a given product.
 * <p>
 * Each implementation handles a specific product type (NORMAL, SEASONAL, EXPIRABLE).
 * This avoids "if/else" branching and keeps the code compliant with SRP and OCP.
 * </p>
 */
public interface ProductProcessor {

    /**
     * Indicates whether this processor can handle the given product.
     *
     * @param product product to be checked
     * @return true if this processor supports the product, false otherwise
     */
    boolean supports(Product product);

    /**
     * Applies the business rules on the given product.
     * <p>
     * Implementations may update product stock, persist changes and send notifications
     * through {@code NotificationService}.
     * </p>
     *
     * @param product product to process
     * @throws IllegalArgumentException if the product is invalid for processing
     */
    void process(Product product);
}
