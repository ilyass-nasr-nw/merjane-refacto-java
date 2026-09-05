package com.nimbleways.springboilerplate.services.products;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.ProductType;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
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

@UnitTest
@ExtendWith(MockitoExtension.class)
class ExpirableProductProcessorTests {

    private static final LocalDate TODAY = LocalDate.of(2024, 6, 15);
    private static final Clock CLOCK = Clock.fixed(TODAY.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);

    @Mock
    private ProductRepository productRepository;
    @Mock
    private NotificationService notificationService;

    private ExpirableProductProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new ExpirableProductProcessor(productRepository, notificationService, CLOCK);
    }

    @Test
    void decrementsStockBeforeExpiry() {
        Product butter = expirableProduct("Butter", 30, TODAY.plusDays(26));

        processor.process(butter);

        assertThat(butter.getAvailable()).isEqualTo(29);
        verify(productRepository).save(butter);
        verifyNoInteractions(notificationService);
    }

    @Test
    void marksExpiredProductOutOfStock() {
        LocalDate expiry = TODAY.minusDays(2);
        Product milk = expirableProduct("Milk", 6, expiry);

        processor.process(milk);

        assertThat(milk.getAvailable()).isZero();
        verify(productRepository).save(milk);
        verify(notificationService).sendExpirationNotification("Milk", expiry);
    }

    @Test
    void expiryDayItselfCountsAsExpired() {
        Product yoghurt = expirableProduct("Yoghurt", 4, TODAY);

        processor.process(yoghurt);

        assertThat(yoghurt.getAvailable()).isZero();
        verify(notificationService).sendExpirationNotification("Yoghurt", TODAY);
    }

    @Test
    void freshButOutOfStockIsTreatedAsExpired() {
        LocalDate expiry = TODAY.plusDays(10);
        Product cheese = expirableProduct("Cheese", 0, expiry);

        processor.process(cheese);

        assertThat(cheese.getAvailable()).isZero();
        verify(productRepository).save(cheese);
        verify(notificationService).sendExpirationNotification("Cheese", expiry);
    }

    @Test
    void handlesExpirableProducts() {
        assertThat(processor.supportedType()).isEqualTo(ProductType.EXPIRABLE);
    }

    private static Product expirableProduct(String name, int available, LocalDate expiryDate) {
        return Product.builder()
                .type(ProductType.EXPIRABLE)
                .name(name)
                .available(available)
                .leadTime(15)
                .expiryDate(expiryDate)
                .build();
    }
}
