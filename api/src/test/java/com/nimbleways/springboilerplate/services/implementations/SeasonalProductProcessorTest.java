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
class SeasonalProductProcessorTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private SeasonalProductProcessor processor;

    @Test
    void shouldDecrementStockWhenInSeason() {
        Product p = new Product();
        p.setAvailable(5);
        p.setSeasonStartDate(LocalDate.now().minusDays(5));
        p.setSeasonEndDate(LocalDate.now().plusDays(5));

        processor.process(p);

        assertEquals(4, p.getAvailable());
        verify(productRepository).save(p);
        verifyNoInteractions(notificationService);
    }

    @Test
    void shouldNotifyOutOfStockWhenLeadTimeExceedsSeasonEnd() {
        Product p = new Product();
        p.setAvailable(0);
        p.setLeadTime(10);
        p.setSeasonStartDate(LocalDate.now().minusDays(5));
        p.setSeasonEndDate(LocalDate.now().plusDays(2)); // Ends before lead time finishes
        p.setName("Watermelon");

        processor.process(p);

        assertEquals(0, p.getAvailable());
        verify(productRepository).save(p);
        verify(notificationService).sendOutOfStockNotification("Watermelon");
    }

    @Test
    void shouldNotifyDelayWhenOutOfStockButLeadTimeIsWithinSeason() {
        Product p = new Product();
        p.setAvailable(0);
        p.setLeadTime(2);
        p.setSeasonStartDate(LocalDate.now().minusDays(5));
        p.setSeasonEndDate(LocalDate.now().plusDays(10)); // Plenty of time left in season
        p.setName("Watermelon");

        processor.process(p);

        verify(productRepository).save(p);
        verify(notificationService).sendDelayNotification(2, "Watermelon");
    }
}