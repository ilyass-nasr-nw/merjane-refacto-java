package com.nimbleways.springboilerplate.services.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;

@Component
public class NormalProductHandler implements ProductTypeHandler {

    private static final Logger logger = LoggerFactory.getLogger(NormalProductHandler.class);

    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    public NormalProductHandler(ProductRepository productRepository, NotificationService notificationService) {
        this.productRepository = productRepository;
        this.notificationService = notificationService;
    }

    @Override
    public void processProductOrder(Product product) {
        if (product.getAvailable() > 0) {
            logger.debug("Stock available for product {}, decrementing", product.getName());
            decrementStock(product);
        } else {
            logger.info("Product {} out of stock", product.getName());
            handleOutOfStock(product);
        }
    }

    @Override
    public String getSupportedType() {
        return "NORMAL";
    }

    private void decrementStock(Product product) {
        product.setAvailable(product.getAvailable() - 1);
        productRepository.save(product);
    }

    private void handleOutOfStock(Product product) {
        int leadTime = product.getLeadTime();
        if (leadTime > 0) {
            logger.info("Notifying delay of {} days for product {}", leadTime, product.getName());
            updateLeadTimeAndNotify(leadTime, product);
        }
    }

    private void updateLeadTimeAndNotify(int leadTime, Product product) {
        product.setLeadTime(leadTime);
        productRepository.save(product);
        notificationService.sendDelayNotification(leadTime, product.getName());
    }
}
