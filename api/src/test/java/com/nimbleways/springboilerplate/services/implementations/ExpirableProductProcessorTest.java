package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpirableProductProcessorTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ExpirableProductProcessor processor;

    @Test
    void shouldDecrementStockWhenNotExpired() {
        Product p = new Product();
        p.setAvailable(5);
        p.setExpiryDate(LocalDate.now().plusDays(10));

        processor.process(p);

        assertEquals(4, p.getAvailable());
        verify(productRepository).save(p);
        verifyNoInteractions(notificationService);
    }

    @Test
    void shouldNotifyExpirationWhenExpired() {
        Product p = new Product();
        p.setAvailable(5);
        p.setExpiryDate(LocalDate.now().minusDays(1));
        p.setName("Milk");

        processor.process(p);

        assertEquals(0, p.getAvailable());
        verify(productRepository).save(p);
        verify(notificationService).sendExpirationNotification("Milk", p.getExpiryDate());
    }
}