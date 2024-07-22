package com.nimbleways.springboilerplate.services;

import org.springframework.stereotype.Service;

import com.nimbleways.springboilerplate.entities.Product;

@Service
public interface ProductService {
	public void notifyDelay(int leadTime, Product product);
    public void handleSeasonalProduct(Product product);
    public void handleExpiredProduct(Product product);
    public void handleFlashableProduct(Product product);
}
