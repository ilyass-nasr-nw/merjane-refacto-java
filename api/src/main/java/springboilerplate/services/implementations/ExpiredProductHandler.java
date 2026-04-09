package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.ProductHandler;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ExpiredProductHandler implements ProductHandler {
    private final ProductRepository productRepository;
    private final NotificationServiceImpl notificationService;

    public ExpiredProductHandler(ProductRepository productRepository, NotificationServiceImpl notificationService) {
        this.productRepository = productRepository;
        this.notificationService = notificationService;
    }

    @Override
    public void handle(Product product) {
        boolean isAvailableAndNotExpired = product.getAvailable() > 0 && product.getExpiryDate().isAfter(LocalDate.now());
        if (isAvailableAndNotExpired) {
            this.saveProduct(product);
            return;
        }

        notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
        product.setAvailable(0);
        productRepository.save(product);
    }

    private void saveProduct(Product product) {
        product.setAvailable(product.getAvailable() - 1);
        productRepository.save(product);
    }
}
