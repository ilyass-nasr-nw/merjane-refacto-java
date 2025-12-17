package com.nimbleways.springboilerplate.services.implementations;

import java.time.LocalDate;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    public void notifyDelay(int leadTime, Product product) {
        updateLeadTime(product, leadTime);
        notificationService.sendDelayNotification(leadTime, product.getName());
    }

    public void handleSeasonalProduct(Product product) {
        if (seasonWillEndBeforeRestock(product)) {
            markOutOfStock(product);
            notifyOutOfStock(product);
            return;
        }
        if (seasonNotStartedYet(product)) {
            notifyOutOfStock(product);
            save(product);
            return;
        }
        notifyDelay(product.getLeadTime(), product);
    }

    public void handleExpiredProduct(Product product) {
        if (isValidAndAvailable(product)) {
            decrementStock(product);
            return;
        }
        expireProduct(product);
    }

    private boolean seasonWillEndBeforeRestock(Product product) {
        return LocalDate.now()
                .plusDays(product.getLeadTime())
                .isAfter(product.getSeasonEndDate());
    }

    private boolean seasonNotStartedYet(Product product) {
        return product.getSeasonStartDate().isAfter(LocalDate.now());
    }

    private boolean isValidAndAvailable(Product product) {
        return product.getAvailable() > 0
                && product.getExpiryDate().isAfter(LocalDate.now());
    }

    private void decrementStock(Product product) {
        product.setAvailable(product.getAvailable() - 1);
        save(product);
    }

    private void updateLeadTime(Product product, int leadTime) {
        product.setLeadTime(leadTime);
        save(product);
    }

    private void expireProduct(Product product) {
        product.setAvailable(0);
        save(product);
        notificationService.sendExpirationNotification(
                product.getName(),
                product.getExpiryDate()
        );
    }

    private void markOutOfStock(Product product) {
        product.setAvailable(0);
        save(product);
    }

    private void notifyOutOfStock(Product product) {
        notificationService.sendOutOfStockNotification(product.getName());
    }

    private void save(Product product) {
        productRepository.save(product);
    }
}
