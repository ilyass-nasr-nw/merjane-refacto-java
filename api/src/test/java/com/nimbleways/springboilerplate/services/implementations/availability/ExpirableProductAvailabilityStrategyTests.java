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
public class ExpirableProductAvailabilityStrategyTests {

    private static final LocalDate TODAY = LocalDate.of(2026, 5, 13);
    private static final Clock FIXED_CLOCK =
            Clock.fixed(TODAY.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ProductService productService;

    private ExpirableProductAvailabilityStrategy strategy;

    @BeforeEach
    public void setUp() {
        strategy = new ExpirableProductAvailabilityStrategy(
                productRepository,
                notificationService,
                productService,
                FIXED_CLOCK);
    }

    @Test
    public void shouldDecrementProductOnExpiryDate() {
        Product product = expirableProduct(3, 2, TODAY);

        strategy.process(product);

        assertThat(product.getAvailable()).isEqualTo(1);
        verify(productRepository).save(product);
        verifyNoInteractions(notificationService, productService);
    }

    @Test
    public void shouldNotifyDelayWhenOutOfStockBeforeExpiryDate() {
        Product product = expirableProduct(3, 0, TODAY.plusDays(10));

        strategy.process(product);

        assertThat(product.getAvailable()).isEqualTo(0);
        verify(productService).notifyDelay(3, product);
        verifyNoInteractions(notificationService);
    }

    private static Product expirableProduct(int leadTime, int available, LocalDate expiryDate) {
        return new Product(null, leadTime, available, "EXPIRABLE", "Butter", expiryDate, null, null);
    }
}
