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
class SeasonalProductHandlerTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private SeasonalProductHandler handler;

    @Test
    void shouldDecrementStockWhenInSeasonAndAvailable() {
        LocalDate seasonStart = LocalDate.now().minusDays(10);
        LocalDate seasonEnd = LocalDate.now().plusDays(50);
        Product product = new Product(1L, 15, 30, "SEASONAL", "Watermelon", null, seasonStart, seasonEnd);

        handler.processProductOrder(product);

        assertEquals(29, product.getAvailable());
        verify(productRepository, times(1)).save(product);
        verifyNoInteractions(notificationService);
    }

    @Test
    void shouldNotifyDelayWhenOutOfStockButCanFulfillInSeason() {
        LocalDate seasonStart = LocalDate.now().minusDays(10);
        LocalDate seasonEnd = LocalDate.now().plusDays(50);
        Product product = new Product(1L, 15, 0, "SEASONAL", "Strawberry", null, seasonStart, seasonEnd);

        handler.processProductOrder(product);

        assertEquals(0, product.getAvailable());
        assertEquals(15, product.getLeadTime());
        verify(productRepository, times(1)).save(product);
        verify(notificationService, times(1)).sendDelayNotification(15, "Strawberry");
    }

    @Test
    void shouldNotifyOutOfStockWhenSeasonNotStarted() {
        LocalDate seasonStart = LocalDate.now().plusDays(30);
        LocalDate seasonEnd = LocalDate.now().plusDays(90);
        Product product = new Product(1L, 15, 5, "SEASONAL", "Grapes", null, seasonStart, seasonEnd);

        handler.processProductOrder(product);

        assertEquals(0, product.getAvailable());
        verify(productRepository, times(1)).save(product);
        verify(notificationService, times(1)).sendOutOfStockNotification("Grapes");
    }

    @Test
    void shouldNotifyOutOfStockWhenDeliveryWouldBeAfterSeasonEnds() {
        LocalDate seasonStart = LocalDate.now().minusDays(10);
        LocalDate seasonEnd = LocalDate.now().plusDays(10);
        Product product = new Product(1L, 30, 0, "SEASONAL", "Mango", null, seasonStart, seasonEnd);

        handler.processProductOrder(product);

        assertEquals(0, product.getAvailable());
        verify(productRepository, times(1)).save(product);
        verify(notificationService, times(1)).sendOutOfStockNotification("Mango");
    }

    @Test
    void shouldNotifyOutOfStockWhenSeasonEnded() {
        LocalDate seasonStart = LocalDate.now().minusDays(60);
        LocalDate seasonEnd = LocalDate.now().minusDays(5);
        Product product = new Product(1L, 15, 10, "SEASONAL", "Peach", null, seasonStart, seasonEnd);

        handler.processProductOrder(product);

        assertEquals(0, product.getAvailable());
        verify(productRepository, times(1)).save(product);
        verify(notificationService, times(1)).sendOutOfStockNotification("Peach");
    }

    @Test
    void shouldNotifyDelayWhenDeliveryExactlyOnSeasonEnd() {
        LocalDate seasonStart = LocalDate.now().minusDays(10);
        LocalDate seasonEnd = LocalDate.now().plusDays(15);
        Product product = new Product(1L, 15, 0, "SEASONAL", "Cherry", null, seasonStart, seasonEnd);

        handler.processProductOrder(product);

        assertEquals(0, product.getAvailable());
        verify(productRepository, times(1)).save(product);
        verify(notificationService, times(1)).sendDelayNotification(15, "Cherry");
    }

    @Test
    void shouldReturnCorrectSupportedType() {
        assertEquals("SEASONAL", handler.getSupportedType());
    }
}
