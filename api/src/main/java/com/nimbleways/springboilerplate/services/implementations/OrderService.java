package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.mappers.OrderMapper;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.services.IOrderService;
import com.nimbleways.springboilerplate.services.strategies.ProductStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final List<ProductStrategy> strategies;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository, List<ProductStrategy> strategies, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.strategies = strategies;
        this.orderMapper = orderMapper;
    }

    public ProcessOrderResponse processOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (order.getItems() != null) {
            for (Product product : order.getItems()) {
                ProductStrategy strategy = strategies.stream()
                        .filter(s -> s.supports(product.getType()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Unsupported product type: " + product.getType()));

                strategy.process(order, product);
            }
        }

        return orderMapper.toProcessOrderResponse(order);
    }
}
