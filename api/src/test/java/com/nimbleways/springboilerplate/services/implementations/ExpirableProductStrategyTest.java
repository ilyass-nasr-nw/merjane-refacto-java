package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.strategies.ExpirableProductStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ExpirableProductStrategyTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    private ExpirableProductStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new ExpirableProductStrategy(productRepository, notificationService);
    }

    @Test
    void shouldDecreaseAvailableQuantityWhenProductIsAvailableAndNotExpired() {
        Product product = new Product();
        product.setName("Milk");
        product.setAvailable(5);
        product.setExpiryDate(LocalDate.now().plusDays(1));

        strategy.handleOrder(product);

        assertEquals(4, product.getAvailable());
        verify(productRepository).save(product);
        verifyNoInteractions(notificationService);
    }

    @Test
    void shouldSetAvailableToZeroAndNotifyWhenProductIsExpired() {
        Product product = new Product();
        product.setName("Milk");
        product.setAvailable(5);
        product.setExpiryDate(LocalDate.now().minusDays(1));

        strategy.handleOrder(product);

        assertEquals(0, product.getAvailable());
        verify(productRepository).save(product);
        verify(notificationService)
                .sendExpirationNotification("Milk", product.getExpiryDate());
    }
}
