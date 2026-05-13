package com.nimbleways.springboilerplate.services.implementations.availability;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.ProductType;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import java.time.Clock;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class ExpirableProductAvailabilityStrategy implements ProductAvailabilityStrategy {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;
    private final Clock clock;

    public ExpirableProductAvailabilityStrategy(
            ProductRepository productRepository,
            NotificationService notificationService,
            Clock clock) {
        this.productRepository = productRepository;
        this.notificationService = notificationService;
        this.clock = clock;
    }

    @Override
    public boolean supports(ProductType productType) {
        return productType == ProductType.EXPIRABLE;
    }

    @Override
    public void process(Product product) {
        LocalDate today = LocalDate.now(clock);
        if (product.getAvailable() > 0 && product.getExpiryDate().isAfter(today)) {
            decrementAvailable(product);
        } else {
            notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
            product.setAvailable(0);
            productRepository.save(product);
        }
    }

    private void decrementAvailable(Product product) {
        product.setAvailable(product.getAvailable() - 1);
        productRepository.save(product);
    }
}
