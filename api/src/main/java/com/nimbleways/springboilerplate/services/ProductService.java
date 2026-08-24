package com.nimbleways.springboilerplate.services;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Product;

public interface ProductService {

    ProcessOrderResponse processOrder(Long orderId);

    void notifyDelay(int leadTime, Product p);

}
