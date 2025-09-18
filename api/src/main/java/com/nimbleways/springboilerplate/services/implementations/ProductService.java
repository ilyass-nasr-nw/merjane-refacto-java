package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    public ProductService(ProductRepository productRepository, NotificationService notificationService) {
        this.productRepository = productRepository;
        this.notificationService = notificationService;
    }

    public void notifyDelay(int leadTime, Product p) {
        p.setLeadTime(leadTime);
        productRepository.save(p);
        notificationService.sendDelayNotification(leadTime, p.getName());
    }

    public void processNormalProduct(Product p) {
        if (p.getAvailable() > 0) {
            p.setAvailable(p.getAvailable() - 1);
            productRepository.save(p);
        } else {
            int leadTime = p.getLeadTime();
            if (leadTime > 0) {
                notifyDelay(leadTime, p);
            }
        }
    }

    public void processSeasonalProduct(Product p) {
        // Add new season rules
        if ((LocalDate.now().isAfter(p.getSeasonStartDate()) && LocalDate.now().isBefore(p.getSeasonEndDate())
                && p.getAvailable() > 0)) {
            p.setAvailable(p.getAvailable() - 1);
            productRepository.save(p);
        } else {
            handleSeasonalProduct(p);
        }
    }

    public void processExpiredProduct(Product p) {
        if (p.getAvailable() > 0 && p.getExpiryDate().isAfter(LocalDate.now())) {
            p.setAvailable(p.getAvailable() - 1);
            productRepository.save(p);
        } else {
            handleExpiredProduct(p);
        }
    }

    public void handleSeasonalProduct(Product p) {
        if (LocalDate.now().plusDays(p.getLeadTime()).isAfter(p.getSeasonEndDate())) {
            notificationService.sendOutOfStockNotification(p.getName());
            p.setAvailable(0);
            productRepository.save(p);
        } else if (p.getSeasonStartDate().isAfter(LocalDate.now())) {
            notificationService.sendOutOfStockNotification(p.getName());
            productRepository.save(p);
        } else {
            notifyDelay(p.getLeadTime(), p);
        }
    }

    public void handleExpiredProduct(Product p) {
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