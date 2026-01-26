package com.nimbleways.springboilerplate.services.product.strategy;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.notification.NotificationService;

public interface HandleProductStrategy {
    void handleProduct(Product p);

    default void notifyDelay(int leadTime, Product p, ProductRepository repo, NotificationService notification) {
        p.setLeadTime(leadTime);
        repo.save(p);
        notification.sendDelayNotification(leadTime, p.getName());
    }
}