package com.nimbleways.merjane.services.handlers;

import com.nimbleways.merjane.persistence.entities.Product;
import com.nimbleways.merjane.domain.ProductType;

public interface ProductTypeHandler {
    ProductType getSupportedType();
    void handle(Product product);
}
