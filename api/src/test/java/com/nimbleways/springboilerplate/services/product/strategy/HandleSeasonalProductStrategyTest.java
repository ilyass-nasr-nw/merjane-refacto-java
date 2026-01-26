package com.nimbleways.springboilerplate.services.product.strategy;

import com.nimbleways.springboilerplate.entities.SeasonalProduct;
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
public class HandleSeasonalProductStrategyTest {

    private HandleSeasonalProductStrategy strategy;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        strategy = new HandleSeasonalProductStrategy(productRepository, notificationService);
    }

    @Test
    public void shouldDecrementAvailableWhenInSeasonAndAvailable() {
        // GIVEN
        LocalDate startDate = LocalDate.now().minusDays(5);
        LocalDate endDate = LocalDate.now().plusDays(5);
        SeasonalProduct product = new SeasonalProduct(1L, 10, 5, ProductType.SEASONAL, "Summer Fruits", startDate, endDate);
        when(productRepository.save(product)).thenReturn(product);

        // WHEN
        strategy.handleProduct(product);

        // THEN
        verify(productRepository, times(1)).save(product);
        assertEquals(4, product.getAvailable());
    }


    @Test
    public void shouldNotifyOutOfStockWhenSeasonNotStarted() {
        // GIVEN
        LocalDate startDate = LocalDate.now().plusDays(5);
        LocalDate endDate = LocalDate.now().plusDays(15);
        SeasonalProduct product = new SeasonalProduct(1L, 3, 0, ProductType.SEASONAL, "Winter Fruits", startDate, endDate);
        when(productRepository.save(product)).thenReturn(product);

        // WHEN
        strategy.handleProduct(product);

        // THEN
        verify(productRepository, times(1)).save(product);
        verify(notificationService, times(1)).sendOutOfStockNotification("Winter Fruits");
    }

    @Test
    public void shouldNotifyDelayWhenInSeasonButNotAvailable() {
        // GIVEN
        LocalDate startDate = LocalDate.now().minusDays(5);
        LocalDate endDate = LocalDate.now().plusDays(15); // Far enough for lead time
        SeasonalProduct product = new SeasonalProduct(1L, 3, 0, ProductType.SEASONAL, "Current Fruits", startDate, endDate);
        when(productRepository.save(product)).thenReturn(product);

        // WHEN
        strategy.handleProduct(product);

        // THEN
        verify(productRepository, times(1)).save(product);
        verify(notificationService, times(1)).sendDelayNotification(3, "Current Fruits");
    }
}
