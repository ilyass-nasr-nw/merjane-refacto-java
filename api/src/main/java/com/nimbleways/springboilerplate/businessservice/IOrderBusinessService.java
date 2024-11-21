package com.nimbleways.springboilerplate.businessservice;

import com.nimbleways.springboilerplate.entities.Order;

public interface IOrderBusinessService {

    Order findById(Long orderId);
}
