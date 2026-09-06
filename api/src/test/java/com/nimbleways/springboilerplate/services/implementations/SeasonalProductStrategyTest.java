package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.strategies.SeasonalProductStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeasonalProductStrategyTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    private SeasonalProductStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new SeasonalProductStrategy(productRepository, notificationService);
    }

    @Test
    void shouldDecreaseAvailableQuantityWhenProductIsInsideSeasonAndAvailable() {
        Product product = new Product();
        product.setName("Strawberry");
        product.setAvailable(3);
        product.setLeadTime(2);
        product.setSeasonStartDate(LocalDate.now().minusDays(1));
        product.setSeasonEndDate(LocalDate.now().plusDays(10));

        strategy.handleOrder(product);

        assertEquals(2, product.getAvailable());
        verify(productRepository).save(product);
        verifyNoInteractions(notificationService);
    }

    @Test
    void shouldSendDelayNotificationWhenOutOfStockAndDelayDoesNotExceedSeason() {
        Product product = new Product();
        product.setName("Strawberry");
        product.setAvailable(0);
        product.setLeadTime(2);
        product.setSeasonStartDate(LocalDate.now().minusDays(1));
        product.setSeasonEndDate(LocalDate.now().plusDays(10));

        strategy.handleOrder(product);

        verify(notificationService)
                .sendDelayNotification(2, "Strawberry");
        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldSendOutOfStockNotificationWhenDelayExceedsSeason() {
        Product product = new Product();
        product.setName("Strawberry");
        product.setAvailable(0);
        product.setLeadTime(10);
        product.setSeasonStartDate(LocalDate.now().minusDays(1));
        product.setSeasonEndDate(LocalDate.now().plusDays(3));

        strategy.handleOrder(product);

        verify(notificationService)
                .sendOutOfStockNotification("Strawberry");
        verify(productRepository, never()).save(any());
    }
}
