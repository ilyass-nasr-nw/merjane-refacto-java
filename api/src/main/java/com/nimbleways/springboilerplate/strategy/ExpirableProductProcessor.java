package com.nimbleways.springboilerplate.strategy;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ExpirableProductProcessor implements ProductProcessor {

    private final ProductRepository productRepository;
    private final ProductService productService;

    @Override
    public void process(Product product) {
        if (isProductAvailable(product)) {
            decrementStock(product);
        } else {
            productService.handleExpiredProduct(product);
        }
    }

    private boolean isProductAvailable(Product product) {
        return product.getAvailable() > 0 && product.getExpiryDate().isAfter(LocalDate.now());
    }

    private void decrementStock(Product product) {
        product.setAvailable(product.getAvailable() - 1);
        productRepository.save(product);
    }
}

