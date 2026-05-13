package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class OrderProcessingService {

    private final OrderRepository orderRepository;
    private final ProductAvailabilityService productAvailabilityService;

    public OrderProcessingService(
            OrderRepository orderRepository,
            ProductAvailabilityService productAvailabilityService) {
        this.orderRepository = orderRepository;
        this.productAvailabilityService = productAvailabilityService;
    }

    public ProcessOrderResponse processOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        Set<Product> products = order.getItems();
        for (Product product : products) {
            productAvailabilityService.process(product);
        }

        return new ProcessOrderResponse(order.getId());
    }
}
