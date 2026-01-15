package com.nimbleways.springboilerplate.services;

/**
 * Service responsible for order processing.
 * <p>
 * Processing an order means applying inventory rules on all products contained in the order:
 * decrementing stock when possible and sending notifications when a product is out of stock,
 * seasonal or expired.
 * </p>
 */
public interface OrderService {

    /**
     * Processes an order by its identifier.
     *
     * @param orderId the unique identifier of the order to process
     * @throws IllegalArgumentException if {@code orderId} is null or the order does not exist
     * @throws IllegalStateException if at least one product type cannot be handled
     */
    void processOrder(Long orderId);
}
