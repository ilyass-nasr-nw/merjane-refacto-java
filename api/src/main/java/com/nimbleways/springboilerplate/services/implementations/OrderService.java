package com.nimbleways.springboilerplate.services.implementations;
import org.springframework.stereotype.Service;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.exceptionshandler.OrderException;
import com.nimbleways.springboilerplate.repositories.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public Order getOrderById(long id) {
        return orderRepository.findById(id).orElseThrow(() -> new OrderException("Order with ID " + id + " not found"));
    }
}