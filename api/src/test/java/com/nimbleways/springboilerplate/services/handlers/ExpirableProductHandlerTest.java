package com.nimbleways.springboilerplate.services.handlers;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@UnitTest
class ExpirableProductHandlerTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ExpirableProductHandler handler;

    @Test
    void shouldDecrementStockWhenAvailableAndNotExpired() {
        LocalDate expiryDate = LocalDate.now().plusDays(10);
        Product product = new Product(1L, 15, 30, "EXPIRABLE", "Butter", expiryDate, null, null);

        handler.processProductOrder(product);

        assertEquals(29, product.getAvailable());
        verify(productRepository, times(1)).save(product);
        verifyNoInteractions(notificationService);
    }

    @Test
    void shouldNotifyExpirationWhenProductExpired() {
        LocalDate expiryDate = LocalDate.now().minusDays(2);
        Product product = new Product(1L, 90, 6, "EXPIRABLE", "Milk", expiryDate, null, null);

        handler.processProductOrder(product);

        assertEquals(0, product.getAvailable());
        verify(productRepository, times(1)).save(product);
        verify(notificationService, times(1)).sendExpirationNotification("Milk", expiryDate);
    }

    @Test
    void shouldNotifyExpirationWhenOutOfStockAndNotExpired() {
        LocalDate expiryDate = LocalDate.now().plusDays(5);
        Product product = new Product(1L, 15, 0, "EXPIRABLE", "Yogurt", expiryDate, null, null);

        handler.processProductOrder(product);

        assertEquals(0, product.getAvailable());
        verify(productRepository, times(1)).save(product);
        verify(notificationService, times(1)).sendExpirationNotification("Yogurt", expiryDate);
    }

    @Test
    void shouldNotifyExpirationWhenOutOfStockAndExpired() {
        LocalDate expiryDate = LocalDate.now().minusDays(1);
        Product product = new Product(1L, 15, 0, "EXPIRABLE", "Cheese", expiryDate, null, null);

        handler.processProductOrder(product);

        assertEquals(0, product.getAvailable());
        verify(productRepository, times(1)).save(product);
        verify(notificationService, times(1)).sendExpirationNotification("Cheese", expiryDate);
    }

    @Test
    void shouldDecrementWhenExpiryDateIsToday() {
        LocalDate expiryDate = LocalDate.now().plusDays(1);
        Product product = new Product(1L, 15, 10, "EXPIRABLE", "Cream", expiryDate, null, null);

        handler.processProductOrder(product);

        assertEquals(9, product.getAvailable());
        verify(productRepository, times(1)).save(product);
        verifyNoInteractions(notificationService);
    }

    @Test
    void shouldReturnCorrectSupportedType() {
        assertEquals("EXPIRABLE", handler.getSupportedType());
    }
}
