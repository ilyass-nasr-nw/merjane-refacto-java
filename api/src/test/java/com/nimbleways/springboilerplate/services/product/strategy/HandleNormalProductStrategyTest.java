package com.nimbleways.springboilerplate.services.product.strategy;

import com.nimbleways.springboilerplate.entities.NormalProduct;
import com.nimbleways.springboilerplate.enums.ProductType;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.notification.NotificationService;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@UnitTest
public class HandleNormalProductStrategyTest {

    private HandleNormalProductStrategy strategy;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        strategy = new HandleNormalProductStrategy(productRepository, notificationService);
    }

    @Test
    public void shouldDecrementAvailableWhenProductIsAvailable() {
        // GIVEN
        NormalProduct product = new NormalProduct(1L, 10, 5, ProductType.NORMAL, "Test Product");
        when(productRepository.save(product)).thenReturn(product);

        // WHEN
        strategy.handleProduct(product);

        // THEN
        verify(productRepository, times(1)).save(product);
        verify(notificationService, never()).sendDelayNotification(anyInt(), anyString());
        assertEquals(4, product.getAvailable());
    }

    @Test
    public void shouldNotifyDelayWhenProductIsNotAvailable() {
        // GIVEN
        NormalProduct product = new NormalProduct(1L, 10, 0, ProductType.NORMAL, "Test Product");
        when(productRepository.save(product)).thenReturn(product);

        // WHEN
        strategy.handleProduct(product);

        // THEN
        verify(productRepository, times(1)).save(product);
        verify(notificationService, times(1)).sendDelayNotification(10, "Test Product");
        assertEquals(10, product.getLeadTime());
    }

    @Test
    public void shouldNotNotifyDelayWhenLeadTimeIsZero() {
        // GIVEN
        NormalProduct product = new NormalProduct(1L, 0, 0, ProductType.NORMAL, "Test Product");

        // WHEN
        strategy.handleProduct(product);

        // THEN
        verify(productRepository, never()).save(product);
    }
}
