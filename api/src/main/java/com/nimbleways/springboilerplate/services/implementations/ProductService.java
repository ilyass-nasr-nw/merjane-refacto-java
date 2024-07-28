package com.nimbleways.springboilerplate.services.implementations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;

import static java.time.LocalDate.now;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductService {

    public static final String NORMAL = "NORMAL";
    public static final String SEASONAL = "SEASONAL";
    public static final String EXPIRABLE = "EXPIRABLE";

    public static final String FLASHSALE = "FLASHSALE";

    private final ProductRepository productRepository;
    private final NotificationService notificationService;
    private final OrderRepository orderRepository;

    /**
     * Due to limited time, I could not implement the flashsale product logic.
     * I should have refactor the code to have an abstract class for product and have different implementations for each product type.
     * using the strategy pattern.
     * Because there is uncommon behavior and attributes for each product type.

    private void processFlashsaleProduct(Product product) {
        log.info("Processing flashsale product with id : " + product.getId());
    }*/

    public ProcessOrderResponse processOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        log.info("Processing order with id : " + order.getId());
        Set<Product> products = order.getItems();

        for (Product product : products) {
            switch (product.getType()) {
                case NORMAL -> processNormalProduct(product);
                case SEASONAL -> processSeasonalProduct(product);
                case EXPIRABLE -> processExpirableProduct(product);
                //case FLASHSALE -> processFlashsaleProduct(product);
                default -> log.error("Unknown product type: " + product.getType());
            }
        }

        return new ProcessOrderResponse(order.getId());
    }

    public void notifyDelay(int leadTime, Product product) {
        product.setLeadTime(leadTime);
        productRepository.save(product);
        notificationService.sendDelayNotification(leadTime, product.getName());
    }

    private void handleSeasonalProduct(Product product) {
        if (now().plusDays(product.getLeadTime()).isAfter(product.getSeasonEndDate())) {
            notificationService.sendOutOfStockNotification(product.getName());
            product.setAvailable(0);
            productRepository.save(product);
        } else if (product.getSeasonStartDate().isAfter(now())) {
            notificationService.sendOutOfStockNotification(product.getName());
            productRepository.save(product);
        } else {
            notifyDelay(product.getLeadTime(), product);
        }
    }

    private void handleExpiredProduct(Product product) {
        if (isProductAvailable(product) && hasNotExpired(product)) {
            decreaseAvailability(product);
        } else {
            notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
            product.setAvailable(0);
            productRepository.save(product);
        }
    }

    public void processNormalProduct(Product product) {
        if (isProductAvailable(product)) {
            decreaseAvailability(product);
        } else {
            int leadTime = product.getLeadTime();
            if (leadTime > 0) {
                notifyDelay(leadTime, product);
            }
        }
    }

    private void processSeasonalProduct(Product product) {
        if (isInSeason(product) && isProductAvailable(product)) {
            decreaseAvailability(product);
            return;
        }
        handleSeasonalProduct(product);
    }

    private void processExpirableProduct(Product product) {
        if (isProductAvailable(product) && hasNotExpired(product)) {
            decreaseAvailability(product);
            return;
        }
        handleExpiredProduct(product);
    }


    private void decreaseAvailability(Product product) {
        product.setAvailable(product.getAvailable() - 1);
        productRepository.save(product);
    }

    private static boolean isInSeason(Product product) {
        return now().isAfter(product.getSeasonStartDate()) && now().isBefore(product.getSeasonEndDate());
    }

    private static boolean hasNotExpired(Product product) {
        return product.getExpiryDate().isAfter(now());
    }

    private static boolean isProductAvailable(Product product) {
        return product.getAvailable() > 0;
    }
}