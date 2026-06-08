package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.services.ProductProcessorFactory;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
public class OrderProcessingService {

    private final OrderRepository orderRepository;
    private final ProductProcessorFactory processorFactory;

    public OrderProcessingService(OrderRepository orderRepository, ProductProcessorFactory processorFactory) {
        this.orderRepository = orderRepository;
        this.processorFactory = processorFactory;
    }

    public void processOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));

        Set<Product> products = order.getItems();
        for (Product p : products) {
            processorFactory.getProcessor(p.getType()).process(p);
        }
    }
}