package com.nimbleways.springboilerplate.services.products;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.ProductType;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;

import java.time.Clock;
import java.time.LocalDate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeasonalProductProcessor implements ProductProcessor {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;
    private final Clock clock;

    @Override
    public ProductType supportedType() {
        return ProductType.SEASONAL;
    }

    @Override
    public void process(Product product) {
        LocalDate today = LocalDate.now(clock);

        if (product.isInSeasonOn(today) && product.isInStock()) {
            product.decrementStock();
            productRepository.save(product);
        } else if (product.restockDateFrom(today).isAfter(product.getSeasonEndDate())) {
            // A restock would land after the season closes, so promising a delay
            // would be a lie: the product is gone until next year.
            product.markOutOfStock();
            productRepository.save(product);
            notificationService.sendOutOfStockNotification(product.getName());
        } else if (product.seasonStartsAfter(today)) {
            // Not on sale yet. Whatever is on the shelves stays there.
            productRepository.save(product);
            notificationService.sendOutOfStockNotification(product.getName());
        } else {
            productRepository.save(product);
            notificationService.sendDelayNotification(product.getLeadTime(), product.getName());
        }
    }
}
