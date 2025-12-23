package com.nimbleways.springboilerplate.handler;

import com.nimbleways.springboilerplate.constants.ProductType;
import com.nimbleways.springboilerplate.entities.Product;

public interface ProductHandler {
	boolean supports(ProductType type);

	void manage(Product product);
}
