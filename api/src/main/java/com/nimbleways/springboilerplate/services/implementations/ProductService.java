package com.nimbleways.springboilerplate.services.implementations;

import java.util.List;
import org.springframework.stereotype.Service;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.exceptionshandler.ProductException;
import com.nimbleways.springboilerplate.handler.ProductHandler;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    
	private final List<ProductHandler> productHandlers;

	public void handleProduct(Product product) {
	    getHandlerFor(product).manage(product);
	}

	private ProductHandler getHandlerFor(Product product) {
	    return productHandlers.stream()
	            .filter(h -> h.supports(product.getType()))
	            .findFirst()
	            .orElseThrow(() -> 
	                new ProductException("No handler found for product type: " + product.getType())
	            );
	}
	 }