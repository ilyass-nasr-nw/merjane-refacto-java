package com.nimbleways.springboilerplate.services.handlers;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;

@Component
public class SeasonalProductHandler implements ProductTypeHandler {

    private static final Logger logger = LoggerFactory.getLogger(SeasonalProductHandler.class);

    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    public SeasonalProductHandler(ProductRepository productRepository, NotificationService notificationService) {
        this.productRepository = productRepository;
        this.notificationService = notificationService;
    }

    @Override
    public void processProductOrder(Product product) {
        if (isInSeason(product) && product.getAvailable() > 0) {
            logger.debug("Product {} in season and available, decrementing", product.getName());
            decrementStock(product);
        } else {
            logger.debug("Product {} unavailable or out of season", product.getName());
            handleUnavailableProduct(product);
        }
    }

    @Override
    public String getSupportedType() {
        return "SEASONAL";
    }

    private boolean isInSeason(Product product) {
        LocalDate now = LocalDate.now();
        return now.isAfter(product.getSeasonStartDate()) && now.isBefore(product.getSeasonEndDate());
    }

    private void decrementStock(Product product) {
        product.setAvailable(product.getAvailable() - 1);
        productRepository.save(product);
    }

    private void handleUnavailableProduct(Product product) {
        if (canFulfillWithDelay(product)) {
            logger.info("Product {} can be fulfilled with delay of {} days", product.getName(), product.getLeadTime());
            updateLeadTimeAndNotify(product.getLeadTime(), product);
        } else {
            logger.info("Product {} out of stock (season ended or not started)", product.getName());
            markAsOutOfStock(product);
        }
    }

    private boolean canFulfillWithDelay(Product product) {
        LocalDate deliveryDate = LocalDate.now().plusDays(product.getLeadTime());
        boolean canDeliverInSeason = deliveryDate.isBefore(product.getSeasonEndDate()) || deliveryDate.isEqual(product.getSeasonEndDate());
        boolean seasonStarted = !product.getSeasonStartDate().isAfter(LocalDate.now());
        return seasonStarted && canDeliverInSeason;
    }

    private void updateLeadTimeAndNotify(int leadTime, Product product) {
        product.setLeadTime(leadTime);
        productRepository.save(product);
        notificationService.sendDelayNotification(leadTime, product.getName());
    }

    private void markAsOutOfStock(Product product) {
        notificationService.sendOutOfStockNotification(product.getName());
        product.setAvailable(0);
        productRepository.save(product);
    }
}
