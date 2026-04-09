package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.ProductHandler;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SeasonalProductHandler implements ProductHandler {
    private final ProductRepository productRepository;
    private final NotificationServiceImpl notificationService;

    public SeasonalProductHandler(ProductRepository productRepository, NotificationServiceImpl notificationService) {
        this.productRepository = productRepository;
        this.notificationService = notificationService;
    }

    @Override
    public void handle(Product p) {
        boolean isAfterAndBeforeAndAvailable = LocalDate.now().isAfter(p.getSeasonStartDate()) && LocalDate.now().isBefore(p.getSeasonEndDate()) && p.getAvailable() > 0;
        boolean isLeadTimeAfterSeasonEndDate = LocalDate.now().plusDays(p.getLeadTime()).isAfter(p.getSeasonEndDate());
        boolean isSeasonStartDateAfterNow = p.getSeasonStartDate().isAfter(LocalDate.now());

        if (isAfterAndBeforeAndAvailable) {
            this.saveProduct(p);
            return;
        }
        if (isLeadTimeAfterSeasonEndDate) {
            notificationService.sendOutOfStockNotification(p.getName());
            p.setAvailable(0);
            productRepository.save(p);
        }
        if (isSeasonStartDateAfterNow) {
            notificationService.sendOutOfStockNotification(p.getName());
            productRepository.save(p);
            return;
        }
        notificationService.sendDelayNotification(p.getLeadTime(), p.getName());

    }

    private void saveProduct(Product product) {
        product.setAvailable(product.getAvailable() - 1);
        productRepository.save(product);
    }
}
