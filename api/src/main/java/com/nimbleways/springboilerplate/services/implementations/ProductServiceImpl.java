package com.nimbleways.springboilerplate.services.implementations;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.exceptions.OrderNotFoundException;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.ProductService;
import com.nimbleways.springboilerplate.services.processing.ProductProcessingStrategy;

@Service
public class ProductServiceImpl implements ProductService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final NotificationService notificationService;
    private final List<ProductProcessingStrategy> strategies;

    public ProductServiceImpl(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            NotificationService notificationService,
            List<ProductProcessingStrategy> strategies) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.notificationService = notificationService;
        this.strategies = strategies;
    }

    /**
     * Processes an order by its ID. It retrieves the order, processes each product in the order,
     * and returns a response containing the order ID.
     * @param orderId The ID of the order to process.
     * @return A ProcessOrderResponse containing the processed order ID.
     * @throws OrderNotFoundException if the order with the given ID does not exist.
     */
    @Override
    public ProcessOrderResponse processOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        Set<Product> products = order.getItems();
        for (Product product : products) {
            process(product);
        }

        return new ProcessOrderResponse(order.getId());
    }

    @Override
    public void process(Product product) {
        strategies.stream()
                .filter(strategy -> strategy.supports(product))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported product type: " + product.getType()))
                .process(product);
    }

    @Override
    public void notifyDelay(int leadTime, Product product) {
        product.setLeadTime(leadTime);
        productRepository.save(product);
        notificationService.sendDelayNotification(leadTime, product.getName());
    }

    @Override
    public void decrementAvailability(Product product) {
        product.setAvailable(product.getAvailable() - 1);
        productRepository.save(product);
    }

    @Override
    public void notifyOutOfStock(Product product) {
        notificationService.sendOutOfStockNotification(product.getName());
    }

    @Override
    public void notifyExpiration(Product product) {
        notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
    }

    @Override
    public void save(Product product) {
        productRepository.save(product);
    }
}