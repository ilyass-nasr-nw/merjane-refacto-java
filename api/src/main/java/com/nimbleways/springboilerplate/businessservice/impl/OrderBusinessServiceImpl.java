package com.nimbleways.springboilerplate.businessservice.impl;

import com.nimbleways.springboilerplate.businessservice.IOrderBusinessService;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class OrderBusinessServiceImpl implements IOrderBusinessService {

    private  final OrderRepository orderRepository;


    @Override
    public Order findById(Long orderId) {
        var existedOrder=orderRepository.findById(orderId);
        return existedOrder.orElse(null);
    }
}
