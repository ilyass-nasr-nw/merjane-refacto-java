package com.nimbleways.springboilerplate.services.product.strategy;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.SeasonalProduct;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.notification.NotificationService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
public class HandleSeasonalProductStrategy implements HandleProductStrategy {
    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    @Override
    public void handleProduct(Product p) {
        SeasonalProduct seasonalProduct = (SeasonalProduct) p;
        if ((LocalDate.now().isAfter(seasonalProduct.getSeasonStartDate()) && LocalDate.now().isBefore(seasonalProduct.getSeasonEndDate())
                && seasonalProduct.getAvailable() > 0)) {
            seasonalProduct.setAvailable(p.getAvailable() - 1);
            productRepository.save(seasonalProduct);
        } else {
            handleSeasonalProduct(seasonalProduct);
        }
    }

    public void handleSeasonalProduct(SeasonalProduct p) {
        if (LocalDate.now().plusDays(p.getLeadTime()).isAfter(p.getSeasonEndDate())) {
            notificationService.sendOutOfStockNotification(p.getName());
            p.setAvailable(0);
            productRepository.save(p);
        } else if (p.getSeasonStartDate().isAfter(LocalDate.now())) {
            notificationService.sendOutOfStockNotification(p.getName());
            productRepository.save(p);
        } else {
            notifyDelay(p.getLeadTime(), p, productRepository, notificationService);
        }
    }
}