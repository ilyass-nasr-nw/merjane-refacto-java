package com.nimbleways.springboilerplate.services.implementations.strategy;

import com.nimbleways.springboilerplate.entities.product.NormalProduct;
import com.nimbleways.springboilerplate.entities.product.Product;
import com.nimbleways.springboilerplate.enums.ProductType;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@UnitTest
class ProcessNormalProductStrategyTest {
    private ProductRepository productRepository;
    private NotificationService notificationService;
    private ProcessNormalProductStrategy strategy;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        notificationService = mock(NotificationService.class);

        strategy = spy(new ProcessNormalProductStrategy(productRepository, notificationService));
    }

    @Test
    void processProduct_shouldHandle() {
        Product product = NormalProduct.builder()
                .name("Laptop")
                .available(10)
                .leadTime(0)
                .type(ProductType.NORMAL)
                .build();


        when(productRepository.save(product)).thenReturn(product);

        Product result = strategy.processProduct(product);

        assertThat(result).isEqualTo(product);

        verify(strategy).handleAvailability(product);
        verify(notificationService, never()).sendDelayNotification(anyInt(), anyString());
        verify(productRepository).save(product);
    }

    @Test
    void processProduct_shouldSendNotification() {

        Product product = NormalProduct.builder()
                .name("Phone")
                .available(0)
                .leadTime(5)
                .type(ProductType.NORMAL)
                .build();

        when(productRepository.save(product)).thenReturn(product);

        Product result = strategy.processProduct(product);

        assertThat(result).isEqualTo(product);

        verify(strategy, never()).handleAvailability(any());
        verify(notificationService).sendDelayNotification(5, "Phone");
        verify(productRepository).save(product);
    }


}
