package com.nimbleways.springboilerplate.services.implementations.availability;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.ProductType;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.ProductService;
import org.springframework.stereotype.Component;

@Component
public class NormalProductAvailabilityStrategy implements ProductAvailabilityStrategy {

    private final ProductRepository productRepository;
    private final ProductService productService;

    public NormalProductAvailabilityStrategy(
            ProductRepository productRepository,
            ProductService productService) {
        this.productRepository = productRepository;
        this.productService = productService;
    }

    @Override
    public boolean supports(ProductType productType) {
        return productType == ProductType.NORMAL;
    }

    @Override
    public void process(Product product) {
        if (product.getAvailable() > 0) {
            decrementAvailable(product);
        } else if (product.getLeadTime() > 0) {
            productService.notifyDelay(product.getLeadTime(), product);
        }
    }

    private void decrementAvailable(Product product) {
        product.setAvailable(product.getAvailable() - 1);
        productRepository.save(product);
    }
}
