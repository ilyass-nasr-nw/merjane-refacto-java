package com.nimbleways.springboilerplate.services.implementations.strategy;

import com.nimbleways.springboilerplate.entities.product.ExpirableProduct;
import com.nimbleways.springboilerplate.entities.product.Product;
import com.nimbleways.springboilerplate.enums.ProductType;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import com.nimbleways.springboilerplate.services.product.ProcessProductStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service("EXPIRABLE")
@RequiredArgsConstructor
@Slf4j
public class ProcessExpirableProductStrategy implements ProcessProductStrategy {
    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    @Override
    public Product processProduct(Product product) {
        log.info("Start [processProduct]: {}", product.getName());
        if (product instanceof ExpirableProduct expirableProduct) {
            if (product.getAvailable() > 0 && expirableProduct.getExpiryDate().isAfter(LocalDate.now())) {
                handleAvailability(expirableProduct);
            } else {
                handleExpiredProduct(expirableProduct);
            }
        }
        log.info("End [processProduct]: {}", product.getName());

        return productRepository.save(product);
    }

    private void handleExpiredProduct(ExpirableProduct product) {
        if (product.getAvailable() > 0 && product.getExpiryDate().isAfter(LocalDate.now())) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
        } else {
            notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
            product.setAvailable(0);
            productRepository.save(product);
        }
    }
}
