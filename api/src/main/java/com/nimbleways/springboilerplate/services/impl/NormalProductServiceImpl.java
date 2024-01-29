package com.nimbleways.springboilerplate.services.impl;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NormalProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    NotificationService notificationService;
    @Override
    public void handle(Product product) {
        if (isAvailable(product)) {
            handleAvailableProduct(product);
        } else {
            handleOutOfStockWithLeadTime(product);
        }
    }

    private boolean isAvailable(Product product) {
        return product.getAvailable() > 0;
    }

    private void handleAvailableProduct(Product product) {
        product.setAvailable(product.getAvailable() - 1);
        productRepository.save(product);
    }

    private void handleOutOfStockWithLeadTime(Product product) {
        int leadTime = product.getLeadTime();
        if (leadTime > 0) {
            updateProductLeadTimeAndNotifyDelay(leadTime, product);
        }
    }

    private void updateProductLeadTimeAndNotifyDelay(int leadTime, Product product) {
        product.setLeadTime(leadTime);
        productRepository.save(product);
        notificationService.sendDelayNotification(leadTime, product.getName());
    }
}
