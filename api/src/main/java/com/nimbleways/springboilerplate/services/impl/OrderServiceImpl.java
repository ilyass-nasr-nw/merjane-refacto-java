package com.nimbleways.springboilerplate.services.impl;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.OrderService;
import com.nimbleways.springboilerplate.services.ProductService;
import com.nimbleways.springboilerplate.services.ProductServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductServiceFactory productServiceFactory;

    @Override
    public ProcessOrderResponse processOrder(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            Set<Product> products = order.get().getItems();
            for (Product product : products) {
                productServiceFactory.handleProduct(product);
            }
            return new ProcessOrderResponse(order.get().getId());
        }
        return null;
    }
}
