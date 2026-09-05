package com.nimbleways.springboilerplate.services.products;

import com.nimbleways.springboilerplate.entities.ProductType;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class ProductProcessorResolver {

    private final Map<ProductType, ProductProcessor> processorsByType = new EnumMap<>(ProductType.class);

    public ProductProcessorResolver(List<ProductProcessor> processors) {
        processors.forEach(processor -> processorsByType.put(processor.supportedType(), processor));
    }

    public ProductProcessor resolve(ProductType type) {
        ProductProcessor processor = processorsByType.get(type);
        if (processor == null) {
            throw new IllegalStateException("No processor declared for product type " + type);
        }
        return processor;
    }
}
