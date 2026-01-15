package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.services.ProductProcessor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderServiceImplTest {

    @Test
    void processOrder_shouldDelegateToCorrectProcessor() {
        OrderRepository orderRepository = Mockito.mock(OrderRepository.class);

        ProductProcessor normalProcessor = Mockito.mock(ProductProcessor.class);
        ProductProcessor seasonalProcessor = Mockito.mock(ProductProcessor.class);

        Product p1 = new Product(1L, 10, 1, "NORMAL", "USB", null, null, null);
        Product p2 = new Product(2L, 10, 1, "SEASONAL", "Watermelon", null, null, null);

        Order order = new Order();
        order.setId(100L);
        order.setItems(Set.of(p1, p2));

        Mockito.when(orderRepository.findById(100L)).thenReturn(Optional.of(order));

        Mockito.when(normalProcessor.supports(p1)).thenReturn(true);
        Mockito.when(normalProcessor.supports(p2)).thenReturn(false);

        Mockito.when(seasonalProcessor.supports(p1)).thenReturn(false);
        Mockito.when(seasonalProcessor.supports(p2)).thenReturn(true);

        OrderServiceImpl service = new OrderServiceImpl(orderRepository, List.of(normalProcessor, seasonalProcessor));

        service.processOrder(100L);

        Mockito.verify(normalProcessor).process(p1);
        Mockito.verify(seasonalProcessor).process(p2);
    }

    @Test
    void processOrder_shouldThrow_whenNoProcessorMatches() {
        OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
        ProductProcessor processor = Mockito.mock(ProductProcessor.class);

        Product product = new Product(1L, 10, 1, "UNKNOWN", "Mystery", null, null, null);

        Order order = new Order();
        order.setId(100L);
        order.setItems(Set.of(product));

        Mockito.when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        Mockito.when(processor.supports(product)).thenReturn(false);

        OrderServiceImpl service = new OrderServiceImpl(orderRepository, List.of(processor));

        assertThrows(IllegalStateException.class, () -> service.processOrder(100L));
    }
}
