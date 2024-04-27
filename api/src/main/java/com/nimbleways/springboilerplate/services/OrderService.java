package com.nimbleways.springboilerplate.services;

import com.nimbleways.springboilerplate.entities.Order;

public interface OrderService {
    Long processOrder(Long orderId);
}
