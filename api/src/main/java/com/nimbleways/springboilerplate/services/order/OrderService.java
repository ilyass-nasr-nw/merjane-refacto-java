package com.nimbleways.springboilerplate.services.order;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;

public interface OrderService {
    ProcessOrderResponse processOrder(Long orderId);
}
