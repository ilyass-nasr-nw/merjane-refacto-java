package com.nimbleways.springboilerplate.services.implementations;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.processing.ProductProcessingStrategy;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;
    private final List<ProductProcessingStrategy> strategies;

    public ProductService(
            ProductRepository productRepository,
            NotificationService notificationService,
            List<ProductProcessingStrategy> strategies) {
        this.productRepository = productRepository;
        this.notificationService = notificationService;
        this.strategies = strategies;
    }

    /**
     * Processes the given product using the appropriate strategy based on its type.
     * @param product The product to process.
     * @throws IllegalArgumentException if no strategy supports the product type.
     */
    public void process(Product product) {
        strategies.stream()
                .filter(strategy -> strategy.supports(product))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported product type: " + product.getType()))
                .process(product);
    }

    // Additional helper methods for notifications and product updates
    public void notifyDelay(int leadTime, Product p) {
        p.setLeadTime(leadTime);
        productRepository.save(p);
        notificationService.sendDelayNotification(leadTime, p.getName());
    }

    /**
     * Decrements the availability of the given prodct and saves it to the repository.
     * @param product The product whose availability is to be decremented.
     */
    public void decrementAvailability(Product product) {
        product.setAvailable(product.getAvailable() - 1);
        productRepository.save(product);
    }

    public void notifyOutOfStock(Product product) {
        notificationService.sendOutOfStockNotification(product.getName());
    }

    /**
     * Sets the availability of the given product to 0 and saves it to the repository.
     * @param product The product whose availability is to be set to 0.
     */
    public void notifyExpiration(Product product) {
        notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
    }

    public void save(Product product) {
        productRepository.save(product);
    }
}