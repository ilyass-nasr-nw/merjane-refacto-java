package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.ExpirableProduct;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.services.ProductStrategy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ExpirableProductStrategy implements ProductStrategy {
    @Override
    public Product processOrder(Product product) {
        ExpirableProduct expirableProduct = (ExpirableProduct) product;
        if (expirableProduct.getAvailable() > 0 && expirableProduct.getExpiryDate().isAfter(LocalDate.now())) {
            expirableProduct.setAvailable(expirableProduct.getAvailable() - 1);
            return expirableProduct;
        } else {
            throw new IllegalStateException("Expirable product is expired or out of stock.");
        }
    }
}
