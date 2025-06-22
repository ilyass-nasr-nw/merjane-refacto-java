package com.nimbleways.springboilerplate.services;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Product;

public interface ProductService {
    void handleSeasonalProduct(Product p);
    void handleExpiredProduct(Product p);
    void notifyDelay(int leadTime, Product p);
}