package com.nimbleways.springboilerplate.services.product.strategy;

import com.nimbleways.springboilerplate.entities.ExpirableProduct;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.notification.NotificationService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
public class HandleExpirableProductStrategy implements HandleProductStrategy {


    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    @Override
    public void handleProduct(Product p) {
        ExpirableProduct expirableProduct = (ExpirableProduct) p;
        if (expirableProduct.getAvailable() > 0 && expirableProduct.getExpiryDate().isAfter(LocalDate.now())) {
            expirableProduct.setAvailable(p.getAvailable() - 1);
            productRepository.save(expirableProduct);
        } else {
            handleExpiredProduct(expirableProduct);
        }
    }

    public void handleExpiredProduct(ExpirableProduct p) {
        if (p.getAvailable() > 0 && p.getExpiryDate().isAfter(LocalDate.now())) {
            p.setAvailable(p.getAvailable() - 1);
            productRepository.save(p);
        } else {
            notificationService.sendExpirationNotification(p.getName(), p.getExpiryDate());
            p.setAvailable(0);
            productRepository.save(p);
        }
    }

}