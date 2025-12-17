package com.nimbleways.springboilerplate.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductProcessorFactory {

    private static final String NORMAL = "NORMAL";
    private static final String SEASONAL = "SEASONAL";
    private static final String EXPIRABLE = "EXPIRABLE";

    private final NormalProductProcessor normalProcessor;
    private final SeasonalProductProcessor seasonalProcessor;
    private final ExpirableProductProcessor expirableProcessor;

    public ProductProcessor getProcessor(String type) {
        return switch (type) {
            case NORMAL -> normalProcessor;
            case SEASONAL -> seasonalProcessor;
            case EXPIRABLE -> expirableProcessor;
            default -> throw new IllegalArgumentException("Unknown product type: " + type);
        };
    }
}
