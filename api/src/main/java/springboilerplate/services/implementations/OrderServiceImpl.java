package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.exceptions.OrderNotFoundException;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.services.OrderService;
import com.nimbleways.springboilerplate.services.ProductService;
import org.springframework.stereotype.Service;


@Service
public class OrderServiceImpl implements OrderService {

    private final ProductService productService;
    private final OrderRepository orderRepository;

    public OrderServiceImpl(ProductServiceImpl productService, OrderRepository orderRepository) {
        this.productService = productService;
        this.orderRepository = orderRepository;
    }

    @Override
    public ProcessOrderResponse processOrder(Long orderId) {
        Order orderOptional = this.orderRepository.findById(orderId)
                                                    .orElseThrow(() -> new OrderNotFoundException(orderId));

        productService.process(orderOptional.getItems());
        return new ProcessOrderResponse(orderId);
    }
}
