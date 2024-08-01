package com.nimbleways.springboilerplate.businessservice.impl;

import com.nimbleways.springboilerplate.businessservice.IProductBusinessService;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ProductBusinessServiceImpl implements IProductBusinessService {

    private final ProductRepository productRepository;


    @Override
    public Product findById(Long productId) {
        var existedProduct=productRepository.findById(productId);
        return existedProduct.orElse(null);
    }

    @Override
    public Product findFirstByName(String name) {
        var existedProduct=productRepository.findFirstByName(name);
        return existedProduct.orElse(null);
    }

    @Override
    public void save(Product product) {
        productRepository.save(product);
    }
}
