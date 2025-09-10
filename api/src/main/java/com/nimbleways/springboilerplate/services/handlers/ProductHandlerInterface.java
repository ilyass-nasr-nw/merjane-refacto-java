package com.nimbleways.springboilerplate.services.handlers;
import com.nimbleways.springboilerplate.entities.Product;

public interface  ProductHandlerInterface {
    boolean supports(String productType);
    void process(Product product);
}
