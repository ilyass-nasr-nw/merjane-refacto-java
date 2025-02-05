package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final NotificationService notificationService;

    public void notifyDelay(int leadTime, Product product, List<Product> updatedProducts) {
        product.setLeadTime(leadTime);
        updatedProducts.add(product);
        notificationService.sendDelayNotification(leadTime, product.getName());
    }

    public void handleSeasonalProduct(Product product, List<Product> updatedProducts) {
        if (LocalDate.now().plusDays(product.getLeadTime()).isAfter(product.getSeasonEndDate())) {
            notificationService.sendOutOfStockNotification(product.getName());
            product.setAvailable(0);
            updatedProducts.add(product);
        } else if (product.getSeasonStartDate().isAfter(LocalDate.now())) {
            notificationService.sendOutOfStockNotification(product.getName());
            updatedProducts.add(product);
        } else {
            notifyDelay(product.getLeadTime(), product, updatedProducts);
        }
    }

    public void handleExpiredProduct(Product product, List<Product> updatedProducts) {
        int availableQnt;
        if (product.getAvailable() > 0 && product.getExpiryDate().isAfter(LocalDate.now())) {
            availableQnt = product.getAvailable() - 1;
        } else {
            notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
            availableQnt = 0;
        }
        product.setAvailable(availableQnt);
        updatedProducts.add(product);
    }
}