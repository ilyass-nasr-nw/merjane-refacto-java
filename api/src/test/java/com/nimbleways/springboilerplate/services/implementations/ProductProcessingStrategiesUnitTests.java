package com.nimbleways.springboilerplate.services.implementations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.processing.ExpirableProductProcessingStrategy;
import com.nimbleways.springboilerplate.services.processing.NormalProductProcessingStrategy;
import com.nimbleways.springboilerplate.services.processing.ProductProcessingStrategy;
import com.nimbleways.springboilerplate.services.processing.SeasonalProductProcessingStrategy;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;

@ExtendWith(MockitoExtension.class)
@UnitTest
class ProductProcessingStrategiesUnitTests {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    private NormalProductProcessingStrategy normalStrategy;
    private SeasonalProductProcessingStrategy seasonalStrategy;
    private ExpirableProductProcessingStrategy expirableStrategy;

    @Mock
    private ProductProcessingStrategy strategy;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        normalStrategy = new NormalProductProcessingStrategy(productRepository, notificationService);
        seasonalStrategy = new SeasonalProductProcessingStrategy(productRepository, notificationService);
        expirableStrategy = new ExpirableProductProcessingStrategy(productRepository, notificationService);
    }

    @Test
    void normalProductAvailableShouldDecreaseStock() {
        Product product = product("NORMAL", 10, 3, 7, null, null, null);

        normalStrategy.process(product);

        assertEquals(2, product.getAvailable());
        verify(productRepository, times(1)).save(product);
        verify(notificationService, never()).sendDelayNotification(7, product.getName());
    }

    @Test
    void normalProductOutOfStockShouldSendLeadTimeNotification() {
        Product product = product("NORMAL", 11, 0, 4, null, null, null);

        normalStrategy.process(product);

        assertEquals(0, product.getAvailable());
        verify(productRepository, times(1)).save(product);
        verify(notificationService, times(1)).sendDelayNotification(4, product.getName());
    }

    @Test
    void seasonalProductAvailableDuringSeasonShouldDecreaseStock() {
        Product product = product(
                "SEASONAL",
                12,
                8,
                3,
                null,
                LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(15));

        seasonalStrategy.process(product);

        assertEquals(7, product.getAvailable());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void seasonalProductOutOfStockWithLeadTimeInsideSeasonShouldNotifyDelay() {
        Product product = product(
                "SEASONAL",
                13,
                0,
                2,
                null,
                LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(10));

        seasonalStrategy.process(product);

        verify(productRepository, times(1)).save(product);
        verify(notificationService, times(1)).sendDelayNotification(2, product.getName());
        verify(notificationService, never()).sendOutOfStockNotification(product.getName());
    }

    @Test
    void seasonalProductOutOfStockWithLeadTimeBeyondSeasonShouldBeUnavailableAndNotify() {
        Product product = product(
                "SEASONAL",
                14,
                0,
                20,
                null,
                LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(5));

        seasonalStrategy.process(product);

        assertEquals(0, product.getAvailable());
        verify(productRepository, times(1)).save(product);
        verify(notificationService, times(1)).sendOutOfStockNotification(product.getName());
    }

    @Test
    void seasonalProductBeforeSeasonShouldNotifyUnavailable() {
        Product product = product(
                "SEASONAL",
                15,
                0,
                1,
                null,
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(20));

        seasonalStrategy.process(product);

        verify(productRepository, times(1)).save(product);
        verify(notificationService, times(1)).sendOutOfStockNotification(product.getName());
    }

    @Test
    void expirableProductValidShouldDecreaseStock() {
        Product product = product("EXPIRABLE", 16, 3, 0, LocalDate.now().plusDays(3), null, null);

        expirableStrategy.process(product);

        assertEquals(2, product.getAvailable());
        verify(productRepository, times(1)).save(product);
        verify(notificationService, never()).sendExpirationNotification(product.getName(), product.getExpiryDate());
    }

    @Test
    void expirableProductExpiredShouldBeUnavailableAndNotify() {
        Product product = product("EXPIRABLE", 17, 3, 0, LocalDate.now().minusDays(1), null, null);

        expirableStrategy.process(product);

        assertEquals(0, product.getAvailable());
        verify(productRepository, times(1)).save(product);
        verify(notificationService, times(1)).sendExpirationNotification(product.getName(), product.getExpiryDate());
    }

    @Test
    void expirableProductAlreadyUnavailableShouldRemainUnavailable() {
        Product product = product("EXPIRABLE", 18, 0, 0, LocalDate.now().minusDays(1), null, null);

        expirableStrategy.process(product);

        assertEquals(0, product.getAvailable());
        verify(productRepository, times(1)).save(product);
        verify(notificationService, times(1)).sendExpirationNotification(product.getName(), product.getExpiryDate());
    }

    @Test
    void processShouldFailOnUnknownType() {
        Product unsupported = product("UNKNOWN", 19, 1, 1, null, null, null);
        when(strategy.supports(unsupported)).thenReturn(false);

        ProductService service = new ProductService(productRepository, notificationService, List.of(strategy));

        assertThrows(IllegalArgumentException.class, () -> service.process(unsupported));
    }

    @Test
    void processShouldFailWhenTypeIsNull() {
        Product unsupported = product(null, 20, 1, 1, null, null, null);
        when(strategy.supports(unsupported)).thenReturn(false);

        ProductService service = new ProductService(productRepository, notificationService, List.of(strategy));

        assertThrows(IllegalArgumentException.class, () -> service.process(unsupported));
    }

    private Product product(
            String type,
            long id,
            int available,
            int leadTime,
            LocalDate expiryDate,
            LocalDate seasonStart,
            LocalDate seasonEnd) {
        Product product = new Product();
        product.setId(id);
        product.setType(type);
        product.setAvailable(available);
        product.setLeadTime(leadTime);
        product.setName("Product-" + id);
        product.setExpiryDate(expiryDate);
        product.setSeasonStartDate(seasonStart);
        product.setSeasonEndDate(seasonEnd);
        return product;
    }
}
