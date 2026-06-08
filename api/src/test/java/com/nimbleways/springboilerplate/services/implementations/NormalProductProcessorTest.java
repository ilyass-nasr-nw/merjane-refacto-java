package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NormalProductProcessorTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NormalProductProcessor processor;

    @Test
    void shouldDecrementStockWhenAvailable() {
        Product p = new Product();
        p.setAvailable(5);

        processor.process(p);

        assertEquals(4, p.getAvailable());
        verify(productRepository).save(p);
        verifyNoInteractions(notificationService);
    }

    @Test
    void shouldNotifyDelayWhenOutOfStockAndHasLeadTime() {
        Product p = new Product();
        p.setAvailable(0);
        p.setLeadTime(3);
        p.setName("TV");

        processor.process(p);

        assertEquals(0, p.getAvailable());
        verify(productRepository).save(p);
        verify(notificationService).sendDelayNotification(3, "TV");
    }
}