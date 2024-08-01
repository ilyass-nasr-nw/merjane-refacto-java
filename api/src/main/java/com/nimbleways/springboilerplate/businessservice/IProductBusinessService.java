package com.nimbleways.springboilerplate.businessservice;

import com.nimbleways.springboilerplate.entities.Product;

public interface IProductBusinessService {

    Product findById(Long productId);


    Product findFirstByName(String name);

    void save(Product product);
}
