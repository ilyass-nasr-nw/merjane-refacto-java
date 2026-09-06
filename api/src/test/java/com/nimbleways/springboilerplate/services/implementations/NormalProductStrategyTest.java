package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.strategies.NormalProductStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NormalProductStrategyTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    private NormalProductStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new NormalProductStrategy(productRepository, notificationService);
    }

    @Test
    void shouldDecreaseAvailableQuantityWhenProductIsAvailable() {
        Product product = new Product();
        product.setName("Normal product");
        product.setAvailable(5);
        product.setLeadTime(3);

        strategy.handleOrder(product);

        assertEquals(4, product.getAvailable());
        verify(productRepository).save(product);
        verifyNoInteractions(notificationService);
    }

    @Test
    void shouldSendDelayNotificationWhenProductIsOutOfStock() {
        Product product = new Product();
        product.setName("Normal product");
        product.setAvailable(0);
        product.setLeadTime(3);

        strategy.handleOrder(product);

        verify(notificationService)
                .sendDelayNotification(3, "Normal product");
        verify(productRepository, never()).save(any());
    }
}
