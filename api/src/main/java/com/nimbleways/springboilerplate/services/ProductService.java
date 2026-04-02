package com.nimbleways.springboilerplate.services;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.OrderRepository;

@Service
public class ProductService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private Map<String, ProductProcessor> processors;

    public ProcessOrderResponse processOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Set<Product> products = order.getItems();

        for (Product p : products) {
            if (p == null) {
                throw new IllegalArgumentException("Product in order cannot be null");
            }
            ProductProcessor processor = processors.get(p.getType());
            if (processor != null) {
                processor.processProduct(p);
            } else {
                throw new RuntimeException("No processor found for type: " + p.getType());
            }
        }

        return new ProcessOrderResponse(order.getId());

    }

}