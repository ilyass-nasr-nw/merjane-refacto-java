package com.nimbleways.springboilerplate.services.impl;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Service
public class FlashSaleProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    NotificationService notificationService;

    @Override
    public void handle(Product product) {
        if (isFlashSaleAvailable(product)) {
            processFlashSaleProduct(product);
        } else {
            handleFlashSaleOutOfStock(product);
        }
    }

    private boolean isFlashSaleAvailable(Product product) {
        return isWithinFlashSalePeriod(product) && hasAvailableFlashSaleItems(product);
    }

    private boolean isWithinFlashSalePeriod(Product product) {
        LocalDate now = LocalDate.now();
        return now.isAfter(product.getFlashSaleStartDate()) &&
                now.isBefore(product.getFlashSaleEndDate());
    }

    private boolean hasAvailableFlashSaleItems(Product product) {
        return product.getAvailable() > 0 && product.getAvailable() <= product.getMaxItemsInFlashSale();
    }

    private void processFlashSaleProduct(Product product) {
        product.setAvailable(product.getAvailable() - 1);
        productRepository.save(product);
    }

    private void handleFlashSaleOutOfStock(Product product) {
        if (!isWithinFlashSalePeriod(product)) {
            // Notify customers about unavailability as flash sale period is over
            notificationService.sendOutOfStockNotification(product.getName());
        } else {
            // Notify customers about flash sale being sold out
            notificationService.sendOutOfStockNotification(product.getName());
        }
    }
}
