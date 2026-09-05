package com.nimbleways.springboilerplate.services.products;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.ProductType;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;

import java.time.Clock;
import java.time.Instant;
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

@UnitTest
@ExtendWith(MockitoExtension.class)
class SeasonalProductProcessorTests {

    private static final LocalDate TODAY = LocalDate.of(2024, 6, 15);
    private static final Clock CLOCK = Clock.fixed(TODAY.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);

    @Mock
    private ProductRepository productRepository;
    @Mock
    private NotificationService notificationService;

    private SeasonalProductProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new SeasonalProductProcessor(productRepository, notificationService, CLOCK);
    }

    @Test
    void decrementsStockDuringTheSeason() {
        Product watermelon = seasonalProduct("Watermelon", 30, 15, TODAY.minusDays(2), TODAY.plusDays(58));

        processor.process(watermelon);

        assertThat(watermelon.getAvailable()).isEqualTo(29);
        verify(productRepository).save(watermelon);
        verifyNoInteractions(notificationService);
    }

    @Test
    void marksOutOfStockWhenRestockingComesTooLate() {
        Product pumpkin = seasonalProduct("Pumpkin", 0, 15, TODAY.minusDays(30), TODAY.plusDays(5));

        processor.process(pumpkin);

        assertThat(pumpkin.getAvailable()).isZero();
        verify(productRepository).save(pumpkin);
        verify(notificationService).sendOutOfStockNotification("Pumpkin");
    }

    @Test
    void notifiesUnavailableBeforeTheSeasonStarts() {
        Product grapes = seasonalProduct("Grapes", 30, 15, TODAY.plusDays(180), TODAY.plusDays(240));

        processor.process(grapes);

        assertThat(grapes.getAvailable()).isEqualTo(30);
        verify(productRepository).save(grapes);
        verify(notificationService).sendOutOfStockNotification("Grapes");
    }

    @Test
    void notifiesDelayWhenRestockingComesInTime() {
        Product strawberry = seasonalProduct("Strawberry", 0, 5, TODAY.minusDays(10), TODAY.plusDays(30));

        processor.process(strawberry);

        verify(productRepository).save(strawberry);
        verify(notificationService).sendDelayNotification(5, "Strawberry");
    }

    @Test
    void openingDayOfTheSeasonIsNotInSeasonYet() {
        Product product = seasonalProduct("Asparagus", 30, 5, TODAY, TODAY.plusDays(60));

        processor.process(product);

        assertThat(product.getAvailable()).isEqualTo(30);
        verify(notificationService).sendDelayNotification(5, product.getName());
    }

    @Test
    void closingDayOfTheSeasonIsNoLongerInSeason() {
        Product product = seasonalProduct("Asparagus", 30, 5, TODAY.minusDays(60), TODAY);

        processor.process(product);

        assertThat(product.getAvailable()).isZero();
        verify(notificationService).sendOutOfStockNotification(product.getName());
    }

    @Test
    void handlesSeasonalProducts() {
        assertThat(processor.supportedType()).isEqualTo(ProductType.SEASONAL);
    }

    private static Product seasonalProduct(String name, int available, int leadTime, LocalDate start, LocalDate end) {
        return Product.builder()
                .type(ProductType.SEASONAL)
                .name(name)
                .available(available)
                .leadTime(leadTime)
                .seasonStartDate(start)
                .seasonEndDate(end)
                .build();
    }
}
