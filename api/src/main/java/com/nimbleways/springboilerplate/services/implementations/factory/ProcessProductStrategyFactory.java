package com.nimbleways.springboilerplate.services.implementations.factory;

import com.nimbleways.springboilerplate.enums.ProductType;
import com.nimbleways.springboilerplate.services.product.ProcessProductStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
@Component
public class ProcessProductStrategyFactory {
    private final Map<String, ProcessProductStrategy> strategies;

    public ProcessProductStrategy getProductStrategy(ProductType productType){
       if(Objects.isNull(productType) || !strategies.containsKey(productType.name()))
           throw new IllegalArgumentException("Unexpected product type: " + productType);

       return strategies.get(productType.name());
    }
}
