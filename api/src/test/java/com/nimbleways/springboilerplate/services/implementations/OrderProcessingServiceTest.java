package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.strategy.ProductProcessor;
import com.nimbleways.springboilerplate.strategy.ProductProcessorFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderProcessingServiceTest {

    @Mock
    private ProductProcessorFactory processorFactory;

    @Mock
    private ProductProcessor processor;

    @InjectMocks
    private OrderProcessingService service;

    @Test
    void shouldProcessAllProductsInOrder() {
        Product product1 = mock(Product.class);
        Product product2 = mock(Product.class);

        when(product1.getType()).thenReturn("NORMAL");
        when(product2.getType()).thenReturn("NORMAL");

        Order order = new Order();
        order.setItems(Set.of(product1, product2));

        when(processorFactory.getProcessor("NORMAL")).thenReturn(processor);

        service.process(order);

        verify(processor, times(2)).process(any(Product.class));
    }
}
