package com.nimbleways.springboilerplate.services.processing;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;

@Component
public class SeasonalProductProcessingStrategy implements ProductProcessingStrategy {

    private static final String PRODUCT_TYPE = "SEASONAL";

    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    public SeasonalProductProcessingStrategy(ProductRepository productRepository, NotificationService notificationService) {
        this.productRepository = productRepository;
        this.notificationService = notificationService;
    }

    @Override
    public boolean supports(Product product) {
        return PRODUCT_TYPE.equals(product.getType());
    }

    /**
     * Processes the given product based on its availability and season dates.
     * If the product is in season and available, it decrements the availability.
     * If the product is out of season or not available, it sends appropriate notifications.
     * @param product The product to process.
     */
    @Override
    public void process(Product product) {
        LocalDate currentDate = LocalDate.now();
        boolean inSeason = currentDate.isAfter(product.getSeasonStartDate())
                && currentDate.isBefore(product.getSeasonEndDate());

        if (inSeason && product.getAvailable() > 0) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
            return;
        }

        if (currentDate.plusDays(product.getLeadTime()).isAfter(product.getSeasonEndDate())) {
            notificationService.sendOutOfStockNotification(product.getName());
            product.setAvailable(0);
            productRepository.save(product);
            return;
        }

        if (product.getSeasonStartDate().isAfter(currentDate)) {
            notificationService.sendOutOfStockNotification(product.getName());
            productRepository.save(product);
            return;
        }

        productRepository.save(product);
        notificationService.sendDelayNotification(product.getLeadTime(), product.getName());
    }
}
