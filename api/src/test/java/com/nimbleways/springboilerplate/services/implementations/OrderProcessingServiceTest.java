package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.services.ProductProcessor;
import com.nimbleways.springboilerplate.services.ProductProcessorFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderProcessingServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductProcessorFactory processorFactory;

    @Mock
    private ProductProcessor mockProcessor;

    @InjectMocks
    private OrderProcessingService orderProcessingService;

    @Test
    void shouldProcessAllProductsInOrder() {
        // Arrange
        Product p1 = new Product(); p1.setType("NORMAL");
        Product p2 = new Product(); p2.setType("SEASONAL");
        
        Order order = new Order();
        order.setId(1L);
        order.setItems(Set.of(p1, p2));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(processorFactory.getProcessor("NORMAL")).thenReturn(mockProcessor);
        when(processorFactory.getProcessor("SEASONAL")).thenReturn(mockProcessor);

        // Act
        orderProcessingService.processOrder(1L);

        // Assert
        verify(processorFactory).getProcessor("NORMAL");
        verify(processorFactory).getProcessor("SEASONAL");
        verify(mockProcessor, times(2)).process(any(Product.class));
    }

    @Test
    void shouldThrowExceptionIfOrderNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            orderProcessingService.processOrder(99L);
        });
    }
}