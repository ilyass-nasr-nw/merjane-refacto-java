package com.nimbleways.springboilerplate.services.products;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.ProductType;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@UnitTest
@ExtendWith(MockitoExtension.class)
class NormalProductProcessorTests {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private NotificationService notificationService;

    private NormalProductProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new NormalProductProcessor(productRepository, notificationService);
    }

    @Test
    void decrementsStockWhenAvailable() {
        Product product = normalProduct(30, 15);

        processor.process(product);

        assertThat(product.getAvailable()).isEqualTo(29);
        verify(productRepository).save(product);
        verifyNoInteractions(notificationService);
    }

    @Test
    void notifiesDelayWhenOutOfStock() {
        Product product = normalProduct(0, 15);

        processor.process(product);

        assertThat(product.getAvailable()).isZero();
        verify(productRepository).save(product);
        verify(notificationService).sendDelayNotification(15, "RJ45 Cable");
    }

    @Test
    void doesNothingWhenOutOfStockWithoutLeadTime() {
        Product product = normalProduct(0, 0);

        processor.process(product);

        assertThat(product.getAvailable()).isZero();
        verify(productRepository, never()).save(product);
        verifyNoInteractions(notificationService);
    }

    @Test
    void handlesNormalProducts() {
        assertThat(processor.supportedType()).isEqualTo(ProductType.NORMAL);
    }

    private static Product normalProduct(int available, int leadTime) {
        return Product.builder()
                .type(ProductType.NORMAL)
                .name("RJ45 Cable")
                .available(available)
                .leadTime(leadTime)
                .build();
    }
}
