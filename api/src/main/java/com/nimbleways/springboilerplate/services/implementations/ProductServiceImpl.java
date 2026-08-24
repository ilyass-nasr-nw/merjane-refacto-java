package com.nimbleways.springboilerplate.services.implementations;

import java.time.LocalDate;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.exceptions.BusinessException;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.services.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final OrderRepository orderRepository;

    private final NotificationService notificationService;

    @Override
    public ProcessOrderResponse processOrder(@PathVariable Long orderId) {
        log.info("Start Processing order with ID: {}", orderId);
        var order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException("error.order.not.found"));
        var products = order.getItems();
        for (var product : products) {
            processProduct(product);
        }

        var output = new ProcessOrderResponse(order.getId());
        log.info("End Processing order with ID: {}", orderId);
        return output;
    }

    private void processProduct(Product product) {
        var productType = product.getType();
        switch (productType) {
            case NORMAL -> processNormalProduct(product);
            case SEASONAL -> processSeasonalProduct(product);
            case EXPIRABLE -> processExpirableProduct(product);
            default -> log.warn("Skipping product '{}' with unsupported type '{}'", product.getName(), productType);
        }
    }

    private void processNormalProduct(Product product) {
        log.info("Processing normal product '{}'", product.getName());
        if (product.getAvailable() != null && product.getAvailable() > 0) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
            log.info("Fulfilled normal product '{}'; remaining stock: {}", product.getName(), product.getAvailable());
            return;
        }

        var leadTime = product.getLeadTime();
        if (leadTime != null && leadTime > 0) {
            log.info("Normal product '{}' is unavailable; notifying {} day delay", product.getName(), leadTime);
            notifyDelay(leadTime, product);
        } else {
            log.warn("Normal product '{}' is unavailable with no lead time", product.getName());
        }
    }

    private void processSeasonalProduct(Product product) {
        log.info("Processing seasonal product '{}'", product.getName());
        if (product.getSeasonStartDate() != null &&
                product.getAvailable() != null &&
                LocalDate.now().isAfter(product.getSeasonStartDate()) &&
                LocalDate.now().isBefore(product.getSeasonEndDate()) &&
                product.getAvailable() > 0) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
            log.info("Fulfilled seasonal product '{}' the remaining stock: {}", product.getName(), product.getAvailable());
        } else {
            handleSeasonalProduct(product);
        }
    }

    private void processExpirableProduct(Product product) {
        log.info("Processing expirable product '{}'", product.getName());
        if (product.getAvailable() != null &&
                product.getAvailable() > 0 &&
                product.getExpiryDate().isAfter(LocalDate.now())) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
            log.info("Fulfilled expirable product '{}'; remaining stock: {}", product.getName(), product.getAvailable());
        } else {
            handleExpiredProduct(product);
        }
    }

    @Override
    public void notifyDelay(int leadTime, Product p) {
        p.setLeadTime(leadTime);
        productRepository.save(p);
        notificationService.sendDelayNotification(leadTime, p.getName());
    }

    private void handleSeasonalProduct(Product p) {
        if (LocalDate.now().plusDays(p.getLeadTime()).isAfter(p.getSeasonEndDate())) {
            log.info("Seasonal product '{}' cannot be delivered before the season ends", p.getName());
            notificationService.sendOutOfStockNotification(p.getName());
            p.setAvailable(0);
            productRepository.save(p);
        } else if (p.getSeasonStartDate().isAfter(LocalDate.now())) {
            log.info("Seasonal product '{}' is not yet in season", p.getName());
            notificationService.sendOutOfStockNotification(p.getName());
            productRepository.save(p);
        } else {
            log.info("Seasonal product '{}' is unavailable; notifying {} day delay", p.getName(), p.getLeadTime());
            notifyDelay(p.getLeadTime(), p);
        }
    }

    private void handleExpiredProduct(Product p) {
        if (p.getAvailable() > 0 && p.getExpiryDate().isAfter(LocalDate.now())) {
            p.setAvailable(p.getAvailable() - 1);
            productRepository.save(p);
            log.info("Fulfilled expirable product '{}' remaining stock: {}", p.getName(), p.getAvailable());
        } else {
            log.info("Expirable product '{}' is unavailable or expired; sending expiration notification", p.getName());
            notificationService.sendExpirationNotification(p.getName(), p.getExpiryDate());
            p.setAvailable(0);
            productRepository.save(p);
        }
    }


}