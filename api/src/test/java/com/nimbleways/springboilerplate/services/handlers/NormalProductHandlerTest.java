package com.nimbleways.springboilerplate.services.handlers;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@UnitTest
class NormalProductHandlerTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NormalProductHandler handler;

    @Test
    void shouldDecrementStockWhenProductIsAvailable() {
        Product product = new Product(1L, 10, 5, "NORMAL", "USB Cable", null, null, null);

        handler.processProductOrder(product);

        assertEquals(4, product.getAvailable());
        verify(productRepository, times(1)).save(product);
        verifyNoInteractions(notificationService);
    }

    @Test
    void shouldNotifyDelayWhenOutOfStockWithLeadTime() {
        Product product = new Product(1L, 15, 0, "NORMAL", "USB Dongle", null, null, null);

        handler.processProductOrder(product);

        assertEquals(0, product.getAvailable());
        assertEquals(15, product.getLeadTime());
        verify(productRepository, times(1)).save(product);
        verify(notificationService, times(1)).sendDelayNotification(15, "USB Dongle");
    }

    @Test
    void shouldNotNotifyWhenOutOfStockWithZeroLeadTime() {
        Product product = new Product(1L, 0, 0, "NORMAL", "Mouse", null, null, null);

        handler.processProductOrder(product);

        assertEquals(0, product.getAvailable());
        verifyNoInteractions(productRepository);
        verifyNoInteractions(notificationService);
    }

    @Test
    void shouldReturnCorrectSupportedType() {
        assertEquals("NORMAL", handler.getSupportedType());
    }
}
