package com.nimbleways.springboilerplate.services.implementations.availability;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.ProductType;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import com.nimbleways.springboilerplate.services.implementations.ProductService;
import java.time.Clock;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class SeasonalProductAvailabilityStrategy implements ProductAvailabilityStrategy {

    private final ProductRepository productRepository;
    private final ProductService productService;
    private final NotificationService notificationService;
    private final Clock clock;

    public SeasonalProductAvailabilityStrategy(
            ProductRepository productRepository,
            ProductService productService,
            NotificationService notificationService,
            Clock clock) {
        this.productRepository = productRepository;
        this.productService = productService;
        this.notificationService = notificationService;
        this.clock = clock;
    }

    @Override
    public boolean supports(ProductType productType) {
        return productType == ProductType.SEASONAL;
    }

    @Override
    public void process(Product product) {
        LocalDate today = LocalDate.now(clock);
        if (today.isAfter(product.getSeasonStartDate())
                && today.isBefore(product.getSeasonEndDate())
                && product.getAvailable() > 0) {
            decrementAvailable(product);
        } else if (today.plusDays(product.getLeadTime()).isAfter(product.getSeasonEndDate())) {
            notificationService.sendOutOfStockNotification(product.getName());
            product.setAvailable(0);
            productRepository.save(product);
        } else if (product.getSeasonStartDate().isAfter(today)) {
            notificationService.sendOutOfStockNotification(product.getName());
            productRepository.save(product);
        } else {
            productService.notifyDelay(product.getLeadTime(), product);
        }
    }

    private void decrementAvailable(Product product) {
        product.setAvailable(product.getAvailable() - 1);
        productRepository.save(product);
    }
}
