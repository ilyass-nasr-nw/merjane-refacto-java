package com.nimbleways.springboilerplate.services.implementations;

import java.time.LocalDate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;


    // Entry point: handles any product type during order processing
    public void processProduct(Product product) {
        switch (product.getType()) {
            case NORMAL:
                handleNormalProduct(product);
                break;
            case SEASONAL:
                handleSeasonalProduct(product);
                break;
            case EXPIRABLE:
                handleExpirableProduct(product);
                break;
            default:
                throw new IllegalArgumentException("Unknown product type: " + product.getType());
        }
    }

    private void handleNormalProduct(Product product) {
        if (product.isInStock()) {
            product.decrementStock();
            productRepository.save(product);
        } else {
            notifyDelay(product);
        }
    }

    private void handleSeasonalProduct(Product product) {
        LocalDate today = LocalDate.now();

        if (!isWithinSeason(product, today)) {
            notificationService.sendOutOfStockNotification(product.getName());
            product.setAvailable(0);
            productRepository.save(product);
            return;
        }

        if (product.isInStock()) {
            product.decrementStock();
            productRepository.save(product);
        } else if (wouldRestockBeforeSeasonEnds(product, today)) {
            notifyDelay(product);
        } else {
            notificationService.sendOutOfStockNotification(product.getName());
            product.setAvailable(0);
            productRepository.save(product);
        }
    }

    private void handleExpirableProduct(Product product) {
        LocalDate today = LocalDate.now();

        if (product.isInStock() && product.getExpiryDate().isAfter(today)) {
            product.decrementStock();
            productRepository.save(product);
        } else {
            notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
            product.setAvailable(0);
            productRepository.save(product);
        }
    }

    private void notifyDelay(Product product) {
        productRepository.save(product);
        notificationService.sendDelayNotification(product.getLeadTime(), product.getName());
    }

    private boolean isWithinSeason(Product product, LocalDate today) {
        return !today.isBefore(product.getSeasonStartDate()) && !today.isAfter(product.getSeasonEndDate());
    }

    private boolean wouldRestockBeforeSeasonEnds(Product product, LocalDate today) {
        return !today.plusDays(product.getLeadTime()).isAfter(product.getSeasonEndDate());
    }
}