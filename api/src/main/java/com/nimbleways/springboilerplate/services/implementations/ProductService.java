package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@slf4j
public class ProductService {

    private final ProductRepository productRepository;

    private final NotificationService notificationService;

    public void notifyDelay(int leadTime, Product product, List<Product> productsToSave) {
        log.info("Notifying delay for product: {} with lead time: {}", product.getName(), leadTime);
        product.setLeadTime(leadTime);
        productsToSave.add(product);
        notificationService.sendDelayNotification(leadTime, product.getName());
        log.info("Delay notification sent for product: {}", product.getName());
    }

    public void processNormalProduct(Product product, List<Product> productsToSave) {
        log.info("Processing normal product: {}", product.getName());
        if (product.getAvailable() > 0) {
            updateStock(product.getAvailable() - 1, product, productsToSave);
        } else {
            int leadTime = product.getLeadTime();
            if (leadTime > 0) {
                notifyDelay(leadTime, product, productsToSave);
            }
        }
        log.info("Finished processing normal product: {}", product.getName());
    }

    public void processSeasonalProduct(Product product, List<Product> productsToSave) {
        log.info("Processing seasonal product: {}", product.getName());
        LocalDate now = LocalDate.now();
        if ((now.isAfter(product.getSeasonStartDate()) && now.isBefore(product.getSeasonEndDate())
                && product.getAvailable() > 0)) {
            updateStock(product.getAvailable(), product, productsToSave);
        } else {
            if (now.plusDays(product.getLeadTime()).isAfter(product.getSeasonEndDate())) {
                notificationService.sendOutOfStockNotification(product.getName());
                updateStock(0, product, productsToSave);
            } else if (product.getSeasonStartDate().isAfter(LocalDate.now())) {
                notificationService.sendOutOfStockNotification(product.getName());
                productsToSave.add(product);
            } else {
                notifyDelay(product.getLeadTime(), product, productsToSave);
            }
        }
        log.info("Finished processing seasonal product: {}", product.getName());
    }

    public void processExpirableProduct(Product product, List<Product> productsToSave) {
        log.info("Processing expirable product: {}", product.getName());
        if (product.getAvailable() > 0 && product.getExpiryDate().isAfter(LocalDate.now())) {
            updateStock(product.getAvailable(), product, productsToSave);
        } else {
            notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
            updateStock(0, product, productsToSave);
        }
        log.info("Finished processing expirable product: {}", product.getName());
    }

    private void updateStock(Integer available, Product product, List<Product> productsToSave) {
        log.info("Updating stock for product: {} with available quantity: {}", product.getName(), available);
        product.setAvailable(available);
        productsToSave.add(product);
        log.info("Stock updated for product: {}", product.getName());
    }

    public void massiveSave(List<Product> productsToSave) {
        log.info("Massive save of products started with size: {}", productsToSave.size());
        if (!productsToSave.isEmpty()) {
            productRepository.saveAll(productsToSave);
        }
        log.info("Massive save of products completed.");
    }
}