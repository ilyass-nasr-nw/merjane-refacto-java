package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.strategy.ProductProcessor;
import com.nimbleways.springboilerplate.strategy.ProductProcessorFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderProcessingService {

    private final ProductProcessorFactory productProcessorFactory;

    public void process(Order order) {
        for (Product product : order.getItems()) {
            ProductProcessor processor = productProcessorFactory.getProcessor(product.getType());
            processor.process(product);
        }
    }
}

