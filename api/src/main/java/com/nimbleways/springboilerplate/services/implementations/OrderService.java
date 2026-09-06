package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.services.implementations.exceptions.OrderNotFoundException;
import com.nimbleways.springboilerplate.services.implementations.strategies.ProductStrategyFactory;
import com.nimbleways.springboilerplate.services.implementations.strategies.ProductStrategy;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class OrderService {
    private OrderRepository orderRepository;

    private ProductStrategyFactory productStrategyFactory;

    @Transactional
    public ProcessOrderResponse processOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException(orderId)
                );

        order.getItems().forEach(product -> {
            ProductStrategy strategy = productStrategyFactory.getProductStrategy(product.getType());
            strategy.handleOrder(product);
        });

        return new ProcessOrderResponse(order.getId());
    }
}
