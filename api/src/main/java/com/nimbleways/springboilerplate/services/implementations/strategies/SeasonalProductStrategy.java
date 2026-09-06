package com.nimbleways.springboilerplate.services.implementations.strategies;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class SeasonalProductStrategy implements ProductStrategy {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    @Override
    public String getProductType() {
        return "SEASONAL";
    }

    @Override
    public void handleOrder(Product product) {
        LocalDate today = LocalDate.now();

        boolean isDuringSeason =
                today.isAfter(product.getSeasonStartDate())
                        && today.isBefore(product.getSeasonEndDate());

        if (isDuringSeason && product.getAvailable() > 0) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
            return;
        }

        LocalDate restockingDate = today.plusDays(product.getLeadTime());

        if (restockingDate.isAfter(product.getSeasonEndDate())
                || today.isBefore(product.getSeasonStartDate())) {
            notificationService.sendOutOfStockNotification(product.getName());
        } else {
            notificationService.sendDelayNotification(
                    product.getLeadTime(),
                    product.getName()
            );
        }
    }
}
