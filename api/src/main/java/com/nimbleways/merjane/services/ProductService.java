package com.nimbleways.merjane.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nimbleways.merjane.persistence.entities.Product;
import com.nimbleways.merjane.persistence.repositories.OrderRepository;
import com.nimbleways.merjane.services.handlers.ProductTypeHandler;

@Service
public class ProductService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private List<ProductTypeHandler> handlers;

    @Transactional
    public Long processOrder(Long orderId) {
        var order = orderRepository.findById(orderId).orElseThrow();
        order.getItems().forEach(this::processProduct);
        return order.getId();
    }

    private void processProduct(Product product) {
        handlers.stream()
                .filter(h -> h.getSupportedType() == product.getType())
                .findFirst()
                .ifPresent(h -> h.handle(product));
    }
}
