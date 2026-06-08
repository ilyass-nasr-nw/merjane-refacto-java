package com.nimbleways.springboilerplate.services;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductProcessorFactory {

    private final List<ProductProcessor> processors;

    public ProductProcessorFactory(List<ProductProcessor> processors) {
        this.processors = processors;
    }

    public ProductProcessor getProcessor(String type) {
        return processors.stream()
            .filter(processor -> processor.supports(type))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown product type: " + type));
    }
}