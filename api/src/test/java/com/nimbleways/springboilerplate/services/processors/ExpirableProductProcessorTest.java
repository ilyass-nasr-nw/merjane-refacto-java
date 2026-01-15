package com.nimbleways.springboilerplate.services.processors;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExpirableProductProcessorTest {

    private final ProductRepository productRepository = Mockito.mock(ProductRepository.class);
    private final NotificationService notificationService = Mockito.mock(NotificationService.class);

    private final Clock fixedClock = Clock.fixed(
            Instant.parse("2026-01-15T00:00:00Z"),
            ZoneId.of("UTC")
    );

    private final ExpirableProductProcessor processor =
            new ExpirableProductProcessor(productRepository, notificationService, fixedClock);

    @Test
    void process_shouldDecrementStock_whenNotExpired_andHasStock() {
        LocalDate expiry = LocalDate.of(2026, 1, 20);
        Product product = new Product(1L, 15, 3, "EXPIRABLE", "Butter", expiry, null, null);

        processor.process(product);

        assertEquals(2, product.getAvailable());
        Mockito.verify(productRepository).save(product);
        Mockito.verifyNoInteractions(notificationService);
    }

    @Test
    void process_shouldSetStockToZero_andNotifyExpiration_whenExpired() {
        LocalDate expiry = LocalDate.of(2026, 1, 10);
        Product product = new Product(1L, 15, 3, "EXPIRABLE", "Milk", expiry, null, null);

        processor.process(product);

        assertEquals(0, product.getAvailable());
        Mockito.verify(notificationService).sendExpirationNotification("Milk", expiry);
        Mockito.verify(productRepository).save(product);
    }
}
