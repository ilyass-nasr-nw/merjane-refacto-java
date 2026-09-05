package com.nimbleways.springboilerplate.services.products;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.ProductType;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NormalProductProcessor implements ProductProcessor {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    @Override
    public ProductType supportedType() {
        return ProductType.NORMAL;
    }

    @Override
    public void process(Product product) {
        if (product.isInStock()) {
            product.decrementStock();
            productRepository.save(product);
        } else if (product.hasLeadTime()) {
            productRepository.save(product);
            notificationService.sendDelayNotification(product.getLeadTime(), product.getName());
        }
        // Out of stock with no lead time: we have nothing to announce.
    }
}
