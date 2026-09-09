package com.nimbleways.springboilerplate.services.strategies;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;

public interface ProductStrategy {
    boolean supports(String productType);
    Order process(Order order, Product product);
}
