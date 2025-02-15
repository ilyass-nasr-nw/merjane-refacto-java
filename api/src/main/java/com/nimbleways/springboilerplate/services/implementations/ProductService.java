package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    private final NotificationService notificationService;

    public void handleNormalProduct(Product product, List<Product> productsToSave) {
        log.info("Start service handleNormalProduct | product : {}", product);
        if (product.getAvailable() > 0) {
            decrementStock(product, productsToSave);
        } else if (product.getLeadTime() > 0) {
            notificationService.sendDelayNotification(product.getLeadTime(), product.getName());
        }
        log.info("End service handleNormalProduct | product : {}", product);
    }

    public void handleSeasonalProduct(Product product, List<Product> productsToSave) {
        log.info("Start service handleSeasonalProduct | product : {}", product);
        LocalDate now = LocalDate.now();
        if (product.getAvailable() > 0) {
            log.info("Product is available");
            if (now.isAfter(product.getSeasonStartDate()) && now.isBefore(product.getSeasonEndDate())) {
                decrementStock(product, productsToSave);
            }

        } else {
            log.info("Product is not available");
            if (now.plusDays(product.getLeadTime()).isBefore(product.getSeasonEndDate()) && now.isAfter(product.getSeasonStartDate())) {
                notificationService.sendDelayNotification(product.getLeadTime(), product.getName());

            } else if (now.plusDays(product.getLeadTime()).isAfter(product.getSeasonEndDate())) {
                notificationService.sendOutOfStockNotification(product.getName());
            }
        }
        log.info("End service handleSeasonalProduct | product : {}", product);

    }

    public void handleExpiredProduct(Product product, List<Product> productsToSave) {
        log.info("Start service handleExpiredProduct | product : {}", product);
        if (product.getAvailable() > 0 && product.getExpiryDate().isAfter(LocalDate.now())) {
            log.info("Product is available");
            decrementStock(product, productsToSave);
        } else {
            log.info("Product is not available");
            notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
        }
        log.info("End service handleExpiredProduct | product : {}", product);
    }

    public void updateProducts (List<Product> products){
        log.info("Start service updateProducts | products : {}", products);
        productRepository.saveAll(products);
        log.info("End service updateProducts | products : {}", products);
    }

    private void decrementStock(Product product, List<Product> updatedProducts) {
        log.info("Decrementing stock for product : {}", product);
        product.setAvailable(product.getAvailable() - 1);
        updatedProducts.add(product);
    }
}