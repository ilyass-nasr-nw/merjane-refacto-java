package com.nimbleways.springboilerplate.services.handlers;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;

@Component
public class ExpirableProductHandler implements ProductTypeHandler {

    private static final Logger logger = LoggerFactory.getLogger(ExpirableProductHandler.class);

    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    public ExpirableProductHandler(ProductRepository productRepository, NotificationService notificationService) {
        this.productRepository = productRepository;
        this.notificationService = notificationService;
    }

    @Override
    public void processProductOrder(Product product) {
        if (isAvailableAndNotExpired(product)) {
            logger.debug("Product {} available and not expired, decrementing", product.getName());
            decrementStock(product);
        } else {
            logger.info("Product {} expired (expiry: {})", product.getName(), product.getExpiryDate());
            markAsExpiredAndNotify(product);
        }
    }

    @Override
    public String getSupportedType() {
        return "EXPIRABLE";
    }

    private boolean isAvailableAndNotExpired(Product product) {
        return product.getAvailable() > 0 && product.getExpiryDate().isAfter(LocalDate.now());
    }

    private void decrementStock(Product product) {
        product.setAvailable(product.getAvailable() - 1);
        productRepository.save(product);
    }

    private void markAsExpiredAndNotify(Product product) {
        notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
        product.setAvailable(0);
        productRepository.save(product);
    }
}
