package com.nimbleways.springboilerplate.services.implementations;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.ProductHelper;
import com.nimbleways.springboilerplate.services.ProductProcessor;

@Service("EXPIRABLE")
public class ExpirableProductProcessor implements ProductProcessor {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    NotificationService notificationService;

    @Override
    public void processProduct(Product product) {
        if (product.getAvailable() > 0 && product.getExpiryDate().isAfter(LocalDate.now())) {
            ProductHelper.decreaseStock(product);
        } else {
            handleExpiredProduct(product);
        }
    }

    public void handleExpiredProduct(Product p) {
        if (p.getAvailable() > 0 && p.getExpiryDate().isAfter(LocalDate.now())) {
            ProductHelper.decreaseStock(p);
        } else {
            notificationService.sendExpirationNotification(p.getName(), p.getExpiryDate());
            ProductHelper.setOutOfStock(p);
        }
    }
}