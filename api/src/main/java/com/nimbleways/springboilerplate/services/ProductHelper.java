package com.nimbleways.springboilerplate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;

@Component
public class ProductHelper {

    @Autowired
    private static ProductRepository productRepository;

    @Autowired
    private static NotificationService notificationService;

    public static void notifyDelay(int leadTime, Product p) {

        if (p == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        productRepository.save(p);

        notificationService.sendDelayNotification(leadTime, p.getName());
    }

    public static void decreaseStock(Product p) {
        p.setAvailable(p.getAvailable() - 1);
        productRepository.save(p);
    }

    public static void setOutOfStock(Product p) {
        p.setAvailable(0);
        productRepository.save(p);
    }
}