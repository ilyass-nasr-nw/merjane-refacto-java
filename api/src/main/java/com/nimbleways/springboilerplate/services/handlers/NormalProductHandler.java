package com.nimbleways.springboilerplate.services.handlers;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.ProductService;

public class NormalProductHandler implements ProductHandlerInterface {

    private final ProductRepository productRepository;
    private final ProductService productService;

    public NormalProductHandler(ProductRepository productRepository, ProductService productService) {
        this.productRepository = productRepository;
        this.productService = productService;
    }

    @Override
    public boolean supports(String productType) {
        return "NORMAL".equalsIgnoreCase(productType);
    }
    
    @Override
    public void process(Product product) {
        if (product.getAvailable() > 0) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);
        } else if (product.getLeadTime() > 0) {
            productService.notifyDelay(product.getLeadTime(), product);
        }
    }
}
