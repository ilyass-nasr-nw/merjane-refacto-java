package com.nimbleways.springboilerplate.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.ProductHelper;
import com.nimbleways.springboilerplate.services.ProductProcessor;

@Service("NORMAL")
public class NormalProductProcessor implements ProductProcessor {

    @Autowired
    ProductRepository productRepository;

    @Override
    public void processProduct(Product product) {
        if (product.getAvailable() > 0) {
            ProductHelper.decreaseStock(product);
        } else {
            int leadTime = product.getLeadTime();
            if (leadTime > 0) {
                ProductHelper.notifyDelay(leadTime, product);
            }
        }
    }

}