package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.order.Order;
import com.nimbleways.springboilerplate.entities.product.Product;
import com.nimbleways.springboilerplate.exceptions.OrderNotFoundException;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.services.order.OrderService;
import com.nimbleways.springboilerplate.services.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductService productService;

    @Override
    public ProcessOrderResponse processOrder(Long orderId) {
        log.info("Start [processOrder] for order: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order for ID: {} not found!"));
        Set<Product> products = order.getItems();
        if(CollectionUtils.isEmpty(products))
            return new ProcessOrderResponse(orderId);

        products.forEach(productService::processProduct);
        log.info("End [processOrder] for order: {}", orderId);
        return new ProcessOrderResponse(order.getId());
    }
}
