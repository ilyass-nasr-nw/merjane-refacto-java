package com.nimbleways.springboilerplate.services.product.strategy;

import com.nimbleways.springboilerplate.entities.ExpirableProduct;
import com.nimbleways.springboilerplate.enums.ProductType;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.notification.NotificationService;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@UnitTest
public class HandleExpirableProductStrategyTest {

    private HandleExpirableProductStrategy strategy;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        strategy = new HandleExpirableProductStrategy(productRepository, notificationService);
    }

    @Test
    public void shouldDecrementAvailableWhenProductIsAvailableAndNotExpired() {
        // GIVEN
        LocalDate futureDate = LocalDate.now().plusDays(10);
        ExpirableProduct product = new ExpirableProduct(1L, 5, 3, ProductType.EXPIRABLE, "Milk", futureDate);
        when(productRepository.save(product)).thenReturn(product);

        // WHEN
        strategy.handleProduct(product);

        // THEN
        verify(productRepository, times(1)).save(product);
        verify(notificationService, never()).sendExpirationNotification(anyString(), any(LocalDate.class));
        assertEquals(2, product.getAvailable());
    }

    @Test
    public void shouldSendExpirationNotificationWhenProductIsExpired() {
        // GIVEN
        LocalDate pastDate = LocalDate.now().minusDays(1);
        ExpirableProduct product = new ExpirableProduct(1L, 5, 3, ProductType.EXPIRABLE, "Expired Milk", pastDate);
        when(productRepository.save(product)).thenReturn(product);

        // WHEN
        strategy.handleProduct(product);

        // THEN
        verify(productRepository, times(1)).save(product);
        verify(notificationService, times(1)).sendExpirationNotification("Expired Milk", pastDate);
        assertEquals(0, product.getAvailable());
    }

    @Test
    public void shouldHandleExpiredProductWhenNoStock() {
        // GIVEN
        LocalDate futureDate = LocalDate.now().plusDays(10);
        ExpirableProduct product = new ExpirableProduct(1L, 5, 0, ProductType.EXPIRABLE, "Not in Stock Milk", futureDate);
        when(productRepository.save(product)).thenReturn(product);

        // WHEN
        strategy.handleProduct(product);

        // THEN
        verify(productRepository, times(1)).save(product);
        verify(notificationService, times(1)).sendExpirationNotification("Not in Stock Milk", futureDate);
        assertEquals(0, product.getAvailable());
    }
}
