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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ProductService productService;

    @Test
    void notifyDelay_shouldUpdateLeadTime_saveProduct_andSendNotification() {
        Product product = new Product();
        product.setName("Product A");

        productService.notifyDelay(5, product);

        assertEquals(5, product.getLeadTime());
        verify(productRepository).save(product);
        verify(notificationService)
                .sendDelayNotification(5, "Product A");
    }

    @Test
    void handleSeasonalProduct_shouldMarkOutOfStock_whenSeasonEndsBeforeRestock() {
        Product product = new Product();
        product.setName("Seasonal A");
        product.setAvailable(10);
        product.setLeadTime(10);
        product.setSeasonStartDate(LocalDate.now().minusDays(5));
        product.setSeasonEndDate(LocalDate.now().plusDays(2));

        productService.handleSeasonalProduct(product);

        assertEquals(0, product.getAvailable());
        verify(notificationService).sendOutOfStockNotification("Seasonal A");
        verify(productRepository).save(product);
        verify(notificationService, never())
                .sendDelayNotification(anyInt(), anyString());
    }

    @Test
    void handleSeasonalProduct_shouldNotifyOutOfStock_whenSeasonNotStartedYet() {
        Product product = new Product();
        product.setName("Seasonal B");
        product.setAvailable(5);
        product.setLeadTime(2);
        product.setSeasonStartDate(LocalDate.now().plusDays(3));
        product.setSeasonEndDate(LocalDate.now().plusDays(10));

        productService.handleSeasonalProduct(product);

        verify(notificationService).sendOutOfStockNotification("Seasonal B");
        verify(productRepository).save(product);
        verify(notificationService, never())
                .sendDelayNotification(anyInt(), anyString());
    }

    @Test
    void handleSeasonalProduct_shouldNotifyDelay_whenSeasonActive() {
        Product product = new Product();
        product.setName("Seasonal C");
        product.setAvailable(5);
        product.setLeadTime(3);
        product.setSeasonStartDate(LocalDate.now().minusDays(1));
        product.setSeasonEndDate(LocalDate.now().plusDays(10));

        productService.handleSeasonalProduct(product);

        assertEquals(3, product.getLeadTime());
        verify(notificationService)
                .sendDelayNotification(3, "Seasonal C");
        verify(productRepository).save(product);
    }

    @Test
    void handleExpiredProduct_shouldDecreaseStock_whenValidAndAvailable() {
        Product product = new Product();
        product.setAvailable(3);
        product.setExpiryDate(LocalDate.now().plusDays(5));

        productService.handleExpiredProduct(product);

        assertEquals(2, product.getAvailable());
        verify(productRepository).save(product);
        verify(notificationService, never())
                .sendExpirationNotification(anyString(), any());
    }

    @Test
    void handleExpiredProduct_shouldExpireProduct_whenExpired() {
        Product product = new Product();
        product.setName("Expired A");
        product.setAvailable(4);
        product.setExpiryDate(LocalDate.now().minusDays(1));

        productService.handleExpiredProduct(product);

        assertEquals(0, product.getAvailable());
        verify(productRepository).save(product);
        verify(notificationService)
                .sendExpirationNotification(
                        "Expired A",
                        product.getExpiryDate()
                );
    }
}
