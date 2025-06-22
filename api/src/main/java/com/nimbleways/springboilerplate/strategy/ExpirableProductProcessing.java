package com.nimbleways.springboilerplate.strategy;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enums.ProductTypeEnum;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ExpirableProductProcessing implements ProductProcessingStrategy{

    private final ProductService productService;
    private final ProductRepository productRepository;


    @Override
    public ProductTypeEnum getType() {
        return ProductTypeEnum.EXPIRABLE;
    }

    @Override
    public void process(Product product) {
        if (isAvailableAndNotExpired(product)) {
            decreaseStock(product);
        } else {
            productService.handleExpiredProduct(product);
        }
    }

    private boolean isAvailableAndNotExpired(Product product) {
        return product.getAvailable() > 0 && product.getExpiryDate().isAfter(LocalDate.now());
    }

    private void decreaseStock(Product product) {
        product.setAvailable(product.getAvailable() - 1);
        productRepository.save(product);
    }
}