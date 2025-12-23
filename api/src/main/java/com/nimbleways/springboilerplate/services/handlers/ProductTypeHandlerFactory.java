package com.nimbleways.springboilerplate.services.handlers;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class ProductTypeHandlerFactory {

    private final Map<String, ProductTypeHandler> handlers;

    public ProductTypeHandlerFactory(List<ProductTypeHandler> handlerList) {
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(ProductTypeHandler::getSupportedType, Function.identity()));
    }

    public ProductTypeHandler getHandler(String productType) {
        ProductTypeHandler handler = handlers.get(productType);
        if (handler == null) {
            throw new IllegalArgumentException("Unsupported product type: " + productType);
        }
        return handler;
    }
}
