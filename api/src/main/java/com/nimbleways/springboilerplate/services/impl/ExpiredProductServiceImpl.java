package com.nimbleways.springboilerplate.services.impl;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Service
public class ExpiredProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    NotificationService notificationService;

    @Override
    public void handle(Product product) {
        if (isAvailableAndNotExpired(product)) {
            handleAvailableAndNotExpiredProduct(product);
        } else {
            handleOutOfStockOrExpired(product);
        }
    }

    private boolean isAvailableAndNotExpired(Product product) {
        return product.getAvailable() > 0 && product.getExpiryDate().isAfter(LocalDate.now());
    }

    private void handleAvailableAndNotExpiredProduct(Product product) {
        product.setAvailable(product.getAvailable() - 1);
        productRepository.save(product);
    }

    private void handleOutOfStockOrExpired(Product product) {
        if (isAvailableAndNotExpired(product)) {
            handleAvailableAndNotExpiredProduct(product);
        } else {
            notifyExpirationAndSetZero(product);
        }
    }

    private void notifyExpirationAndSetZero(Product product) {
        notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
        product.setAvailable(0);
        productRepository.save(product);
    }
}
