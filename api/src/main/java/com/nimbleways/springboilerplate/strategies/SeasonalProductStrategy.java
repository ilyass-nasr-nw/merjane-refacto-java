package com.nimbleways.springboilerplate.strategies;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class SeasonalProductStrategy implements ProductAvailabilityStrategy{
    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    @Override
    public void processOrder(Product product) {
        if (isInSeasonAndAvailable(product)) {
            decrementAvailability(product);
        } else {
            handleSeasonalUnavailability(product);
        }
    }

    private boolean isInSeasonAndAvailable(Product product) {
        LocalDate now = LocalDate.now();
        return product.getSeasonStartDate() != null
                && product.getSeasonEndDate() != null
                && !now.isBefore(product.getSeasonStartDate())
                && now.isBefore(product.getSeasonEndDate())
                && product.getAvailable() != null
                && product.getAvailable() > 0;
    }

    private void decrementAvailability(Product product) {
        product.setAvailable(product.getAvailable() - 1);
        productRepository.save(product);
    }

    private void handleSeasonalUnavailability(Product product) {
        LocalDate now = LocalDate.now();
        Integer leadTime = product.getLeadTime() != null ? product.getLeadTime() : 0;
        LocalDate availabilityDate = now.plusDays(leadTime);

        // Check if product will be out of season by the time it arrives
        if (product.getSeasonEndDate() != null && availabilityDate.isAfter(product.getSeasonEndDate())) {
            markAsUnavailable(product);
        } else if (product.getSeasonStartDate() != null && now.isBefore(product.getSeasonStartDate())) {
            // Season hasn't started yet
            notificationService.sendOutOfStockNotification(product.getName());
            productRepository.save(product);
        } else {
            // Out of stock but within season - notify about delay
            notifyDelay(product, leadTime);
        }
    }

    private void markAsUnavailable(Product product) {
        notificationService.sendOutOfStockNotification(product.getName());
        product.setAvailable(0);
        productRepository.save(product);
    }

    private void notifyDelay(Product product, int leadTime) {
        product.setLeadTime(leadTime);
        productRepository.save(product);
        notificationService.sendDelayNotification(leadTime, product.getName());
    }
}
