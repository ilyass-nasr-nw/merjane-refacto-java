package com.nimbleways.springboilerplate.services;


import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.exceptions.OrderNotFoundException;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class OrderService {

    private final ProductService productService;
    private final OrderRepository orderRepository;


    public ProcessOrderResponse processOrder(final Long orderId) {

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        log.info("@OrderService : Found order by given id {}", order.getId());
        Set<Product> products = order.getItems();
        if(Objects.isNull(products) || products.isEmpty()) {
            throw new IllegalArgumentException("Products cannot be empty");
        }
        log.info("@OrderService : Order {} contains {} products", order.getId(), products.size());
        products.forEach(productService::processProduct);
        log.info("@OrderService : Order {} processed", order.getId());
        return new ProcessOrderResponse(order.getId());
    }
}
