package com.nimbleways.springboilerplate.services;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;

public interface OrderService {
    ProcessOrderResponse processOrder(Long orderId);
}
