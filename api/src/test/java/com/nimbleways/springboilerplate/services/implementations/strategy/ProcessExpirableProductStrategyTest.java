package com.nimbleways.springboilerplate.services.implementations.strategy;

import com.nimbleways.springboilerplate.entities.product.ExpirableProduct;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@UnitTest
class ProcessExpirableProductStrategyTest {
    private ProductRepository productRepository;
    private NotificationService notificationService;
    private ProcessExpirableProductStrategy strategy;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        notificationService = mock(NotificationService.class);
        strategy = spy(new ProcessExpirableProductStrategy(productRepository, notificationService));
    }

    @Test
    void processProduct_availableAndNotExpired_shouldHandleAvailability() {
        ExpirableProduct product = ExpirableProduct.builder()
                .name("Juice")
                .available(5)
                .expiryDate(LocalDate.now().plusDays(5))
                .build();

        strategy.processProduct(product);

        verify(strategy).handleAvailability(product);
        verify(productRepository).save(product);
        verify(notificationService, never()).sendExpirationNotification(any(), any());
    }

    @Test
    void processProduct_unavailableOrExpired_shouldHandleAsExpired() {
        ExpirableProduct product = ExpirableProduct.builder()
                .name("Milk")
                .available(3)
                .expiryDate(LocalDate.now().minusDays(1))
                .build();
        strategy.processProduct(product);

        verify(strategy).processProduct(product);
    }

    @Test
    void handleExpiredProduct_whenExpired_shouldSendNotification() {
        ExpirableProduct product = ExpirableProduct.builder()
                .name("Yogurt")
                .available(2)
                .expiryDate(LocalDate.now().minusDays(2))
                .build();
        strategy.processProduct(product);

        verify(notificationService).sendExpirationNotification("Yogurt", product.getExpiryDate());
        verify(productRepository).save(product);
    }

}
