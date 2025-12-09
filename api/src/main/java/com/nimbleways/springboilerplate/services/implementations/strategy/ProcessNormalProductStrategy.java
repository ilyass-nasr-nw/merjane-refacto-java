package com.nimbleways.springboilerplate.services.implementations.strategy;

import com.nimbleways.springboilerplate.entities.product.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import com.nimbleways.springboilerplate.services.product.ProcessProductStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service("NORMAL")
@RequiredArgsConstructor
@Slf4j
public class ProcessNormalProductStrategy implements ProcessProductStrategy {
    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    @Override
    public Product processProduct(Product product) {
        log.info("Start [processProduct]: {}", product.getName());
        if (product.getAvailable() > 0) {
            handleAvailability(product);
        } else {
            int leadTime = product.getLeadTime();
            if (leadTime > 0) {
                product.setLeadTime(leadTime);
                notificationService.sendDelayNotification(leadTime, product.getName());
            }

        }
        log.info("End [processProduct]: {}", product.getName());

        return productRepository.save(product);
    }
}
