package com.nimbleways.springboilerplate.services.implementations.availability;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import com.nimbleways.springboilerplate.services.implementations.ProductService;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@UnitTest
public class SeasonalProductAvailabilityStrategyTests {

    private static final LocalDate TODAY = LocalDate.of(2026, 5, 13);
    private static final Clock FIXED_CLOCK =
            Clock.fixed(TODAY.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductService productService;

    @Mock
    private NotificationService notificationService;

    private SeasonalProductAvailabilityStrategy strategy;

    @BeforeEach
    public void setUp() {
        strategy = new SeasonalProductAvailabilityStrategy(
                productRepository,
                productService,
                notificationService,
                FIXED_CLOCK);
    }

    @Test
    public void shouldDecrementProductOnSeasonStartDate() {
        Product product = seasonalProduct(5, 2, TODAY, TODAY.plusDays(10));

        strategy.process(product);

        assertThat(product.getAvailable()).isEqualTo(1);
        verify(productRepository).save(product);
        verifyNoInteractions(productService, notificationService);
    }

    @Test
    public void shouldDecrementProductOnSeasonEndDate() {
        Product product = seasonalProduct(5, 2, TODAY.minusDays(10), TODAY);

        strategy.process(product);

        assertThat(product.getAvailable()).isEqualTo(1);
        verify(productRepository).save(product);
        verifyNoInteractions(productService, notificationService);
    }

    @Test
    public void shouldNotifyDelayWhenRestockDateIsSeasonEndDate() {
        Product product = seasonalProduct(5, 0, TODAY.minusDays(10), TODAY.plusDays(5));

        strategy.process(product);

        assertThat(product.getAvailable()).isEqualTo(0);
        verify(productService).notifyDelay(5, product);
        verifyNoInteractions(notificationService);
    }

    private static Product seasonalProduct(
            int leadTime,
            int available,
            LocalDate seasonStartDate,
            LocalDate seasonEndDate) {
        return new Product(null, leadTime, available, "SEASONAL", "Watermelon", null, seasonStartDate, seasonEndDate);
    }
}
