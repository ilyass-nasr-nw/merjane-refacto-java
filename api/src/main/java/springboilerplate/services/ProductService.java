package com.nimbleways.springboilerplate.services;

import com.nimbleways.springboilerplate.entities.Product;

import java.util.Set;

public interface ProductService {
    void process(Set<Product> products);
}
