package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.services.ProductStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class NormalProductStrategy implements ProductStrategy {

    private final ProductService productService;

    public NormalProductStrategy(@Autowired ProductService productService) {
        this.productService = productService;
    }

    @Override
    public Product processOrder(Product product) {
        if (product.getAvailable() > 0) {
            product.setAvailable(product.getAvailable() - 1);
            product = productService.save(product);
        } else {
            int leadTime = product.getLeadTime();
            if (leadTime > 0) {
                productService.notifyDelay(leadTime, product);
            }
        }
        return product;
    }
}
