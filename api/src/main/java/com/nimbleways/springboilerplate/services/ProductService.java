package com.nimbleways.springboilerplate.services;

import com.nimbleways.springboilerplate.entities.Product;

public interface ProductService {

    void process(Product product);

    void notifyDelay(int leadTime, Product product);

    void decrementAvailability(Product product);

    void notifyOutOfStock(Product product);

    void notifyExpiration(Product product);

    void save(Product product);
}