package com.nimbleways.springboilerplate.services.implementations.strategy;

import com.nimbleways.springboilerplate.entities.product.Product;
import com.nimbleways.springboilerplate.entities.product.SeasonalProduct;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import com.nimbleways.springboilerplate.services.product.ProcessProductStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service("SEASONAL")
@RequiredArgsConstructor
@Slf4j
public class ProcessSeasonalProductStrategy implements ProcessProductStrategy {
    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    @Override
    public Product processProduct(Product product) {
        log.info("Start [processProduct]: {}", product.getName());
        if (product instanceof SeasonalProduct seasonalProduct) {
            if ((LocalDate.now().isAfter(seasonalProduct.getSeasonStartDate())
                    && LocalDate.now().isBefore(seasonalProduct.getSeasonEndDate())
                    && seasonalProduct.getAvailable() > 0))
                handleAvailability(product);
            else
                handleSeasonalProduct(seasonalProduct);
        }

        log.info("End [processProduct]: {}", product.getName());

        return productRepository.save(product);
    }

    private void handleSeasonalProduct(SeasonalProduct product) {
        if (LocalDate.now().plusDays(product.getLeadTime()).isAfter(product.getSeasonEndDate())) {
            notificationService.sendOutOfStockNotification(product.getName());
            product.setAvailable(0);
        } else if (product.getSeasonStartDate().isAfter(LocalDate.now())) {
            notificationService.sendOutOfStockNotification(product.getName());
            productRepository.save(product);
        } else {
            product.setLeadTime(product.getLeadTime());
            notificationService.sendDelayNotification(product.getLeadTime(), product.getName());
        }
    }
}
