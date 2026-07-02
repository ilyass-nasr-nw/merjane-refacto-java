package com.nimbleways.springboilerplate.services.implementations;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.ProductService;
import com.nimbleways.springboilerplate.services.processing.ProductProcessingStrategy;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;
    private final List<ProductProcessingStrategy> strategies;

    public ProductServiceImpl(
            ProductRepository productRepository,
            NotificationService notificationService,
            List<ProductProcessingStrategy> strategies) {
        this.productRepository = productRepository;
        this.notificationService = notificationService;
        this.strategies = strategies;
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