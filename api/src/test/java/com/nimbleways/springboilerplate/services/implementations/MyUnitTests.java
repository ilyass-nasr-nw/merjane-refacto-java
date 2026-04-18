package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@UnitTest
public class MyUnitTests {

    @Mock
    private NotificationService notificationService;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductService productService;

    // --- NORMAL ---

    @Test
    public void normalProduct_withStock_shouldDecrementAvailable() {
        Product product = new Product(null, 15, 5, "NORMAL", "RJ45 Cable", null, null, null);
        Mockito.when(productRepository.save(product)).thenReturn(product);

        productService.processProduct(product);

        assertEquals(4, product.getAvailable());
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
        Mockito.verifyNoInteractions(notificationService);
    }

    @Test
    public void normalProduct_outOfStock_withLeadTime_shouldNotifyDelay() {
        Product product = new Product(null, 15, 0, "NORMAL", "RJ45 Cable", null, null, null);
        Mockito.when(productRepository.save(product)).thenReturn(product);

        productService.processProduct(product);

        assertEquals(0, product.getAvailable());
        assertEquals(15, product.getLeadTime());
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
        Mockito.verify(notificationService, Mockito.times(1)).sendDelayNotification(15, "RJ45 Cable");
    }

    @Test
    public void normalProduct_outOfStock_withNoLeadTime_shouldDoNothing() {
        Product product = new Product(null, 0, 0, "NORMAL", "RJ45 Cable", null, null, null);

        productService.processProduct(product);

        Mockito.verifyNoInteractions(productRepository);
        Mockito.verifyNoInteractions(notificationService);
    }

    // --- EXPIRABLE ---

    @Test
    public void expirableProduct_withStockNotExpired_shouldDecrementAvailable() {
        LocalDate futureExpiry = LocalDate.now().plusDays(10);
        Product product = new Product(null, 5, 3, "EXPIRABLE", "Butter", futureExpiry, null, null);
        Mockito.when(productRepository.save(product)).thenReturn(product);

        productService.processProduct(product);

        assertEquals(2, product.getAvailable());
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
        Mockito.verifyNoInteractions(notificationService);
    }

    @Test
    public void expirableProduct_expired_shouldNotifyExpirationAndSetAvailableToZero() {
        LocalDate pastExpiry = LocalDate.now().minusDays(2);
        Product product = new Product(null, 5, 3, "EXPIRABLE", "Milk", pastExpiry, null, null);
        Mockito.when(productRepository.save(product)).thenReturn(product);

        productService.processProduct(product);

        assertEquals(0, product.getAvailable());
        Mockito.verify(notificationService, Mockito.times(1)).sendExpirationNotification("Milk", pastExpiry);
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
    }

    @Test
    public void expirableProduct_outOfStock_shouldNotifyExpirationAndSetAvailableToZero() {
        LocalDate futureExpiry = LocalDate.now().plusDays(5);
        Product product = new Product(null, 5, 0, "EXPIRABLE", "Cheese", futureExpiry, null, null);
        Mockito.when(productRepository.save(product)).thenReturn(product);

        productService.processProduct(product);

        assertEquals(0, product.getAvailable());
        Mockito.verify(notificationService, Mockito.times(1)).sendExpirationNotification("Cheese", futureExpiry);
    }

    // --- SEASONAL ---

    @Test
    public void seasonalProduct_inSeason_withStock_shouldDecrementAvailable() {
        LocalDate seasonStart = LocalDate.now().minusDays(10);
        LocalDate seasonEnd = LocalDate.now().plusDays(60);
        Product product = new Product(null, 5, 10, "SEASONAL", "Watermelon", null, seasonStart, seasonEnd);
        Mockito.when(productRepository.save(product)).thenReturn(product);

        productService.processProduct(product);

        assertEquals(9, product.getAvailable());
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
        Mockito.verifyNoInteractions(notificationService);
    }

    @Test
    public void seasonalProduct_inSeason_outOfStock_restockBeforeSeasonEnd_shouldNotifyDelay() {
        LocalDate seasonStart = LocalDate.now().minusDays(10);
        LocalDate seasonEnd = LocalDate.now().plusDays(60);
        Product product = new Product(null, 5, 0, "SEASONAL", "Watermelon", null, seasonStart, seasonEnd);
        Mockito.when(productRepository.save(product)).thenReturn(product);

        productService.processProduct(product);

        Mockito.verify(notificationService, Mockito.times(1)).sendDelayNotification(5, "Watermelon");
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
    }

    @Test
    public void seasonalProduct_inSeason_outOfStock_restockAfterSeasonEnd_shouldSendOutOfStock() {
        LocalDate seasonStart = LocalDate.now().minusDays(10);
        LocalDate seasonEnd = LocalDate.now().plusDays(3);
        Product product = new Product(null, 10, 0, "SEASONAL", "Watermelon", null, seasonStart, seasonEnd);
        Mockito.when(productRepository.save(product)).thenReturn(product);

        productService.processProduct(product);

        assertEquals(0, product.getAvailable());
        Mockito.verify(notificationService, Mockito.times(1)).sendOutOfStockNotification("Watermelon");
    }

    @Test
    public void seasonalProduct_beforeSeason_restockArrivesBeforeSeasonEnd_shouldSendOutOfStock() {
        LocalDate seasonStart = LocalDate.now().plusDays(30);
        LocalDate seasonEnd = LocalDate.now().plusDays(90);
        Product product = new Product(null, 5, 10, "SEASONAL", "Grapes", null, seasonStart, seasonEnd);
        Mockito.when(productRepository.save(product)).thenReturn(product);

        productService.processProduct(product);

        Mockito.verify(notificationService, Mockito.times(1)).sendOutOfStockNotification("Grapes");
    }

    @Test
    public void seasonalProduct_beforeSeason_restockArrivesAfterSeasonEnd_shouldSendOutOfStockAndSetAvailableToZero() {
        LocalDate seasonStart = LocalDate.now().plusDays(30);
        LocalDate seasonEnd = LocalDate.now().plusDays(40);
        Product product = new Product(null, 50, 10, "SEASONAL", "Grapes", null, seasonStart, seasonEnd);
        Mockito.when(productRepository.save(product)).thenReturn(product);

        productService.processProduct(product);

        assertEquals(0, product.getAvailable());
        Mockito.verify(notificationService, Mockito.times(1)).sendOutOfStockNotification("Grapes");
    }
}