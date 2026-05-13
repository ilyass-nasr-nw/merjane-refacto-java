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
        Order order = orderRepository.findById(orderId).get();
        System.out.println(order);
        Set<Product> products = order.getItems();
        for (Product p : products) {
            productAvailabilityService.process(p);
        }

        return new ProcessOrderResponse(order.getId());
    }
}
