package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enums.ProductType;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@UnitTest
public class ProductServiceTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    // -- NORMAL products --

    @Test
    public void normalProduct_inStock_decrementsAndSaves() {
        Product product = normalProduct("USB Cable", 30, 15);

        productService.processProduct(product);

        assertEquals(29, product.getAvailable());
        verify(productRepository).save(product);
        verifyNoInteractions(notificationService);
    }

    @Test
    public void normalProduct_outOfStock_notifiesDelay() {
        Product product = normalProduct("USB Cable", 0, 15);

        productService.processProduct(product);

        assertEquals(0, product.getAvailable());
        verify(notificationService).sendDelayNotification(15, "USB Cable");
        verify(productRepository).save(product);
    }

    // -- SEASONAL products --

    @Test
    public void seasonalProduct_inStockAndInSeason_decrementsAndSaves() {
        Product product = seasonalProduct("Watermelon", 30, 15,
                LocalDate.now().minusDays(30), LocalDate.now().plusDays(30));

        productService.processProduct(product);

        assertEquals(29, product.getAvailable());
        verify(productRepository).save(product);
        verifyNoInteractions(notificationService);
    }

    @Test
    public void seasonalProduct_outOfStockButRestockBeforeSeasonEnds_notifiesDelay() {
        Product product = seasonalProduct("Watermelon", 0, 10,
                LocalDate.now().minusDays(30), LocalDate.now().plusDays(30));

        productService.processProduct(product);

        verify(notificationService).sendDelayNotification(10, "Watermelon");
        verify(productRepository).save(product);
    }

    @Test
    public void seasonalProduct_outOfStockAndRestockAfterSeason_notifiesOutOfStock() {
        Product product = seasonalProduct("Watermelon", 0, 15,
                LocalDate.now().minusDays(30), LocalDate.now().plusDays(5));

        productService.processProduct(product);

        assertEquals(0, product.getAvailable());
        verify(notificationService).sendOutOfStockNotification("Watermelon");
        verify(productRepository).save(product);
    }

    @Test
    public void seasonalProduct_beforeSeason_notifiesOutOfStock() {
        Product product = seasonalProduct("Grapes", 30, 15,
                LocalDate.now().plusDays(30), LocalDate.now().plusDays(90));

        productService.processProduct(product);

        assertEquals(0, product.getAvailable());
        verify(notificationService).sendOutOfStockNotification("Grapes");
        verify(productRepository).save(product);
    }

    @Test
    public void seasonalProduct_afterSeason_notifiesOutOfStock() {
        Product product = seasonalProduct("Grapes", 30, 15,
                LocalDate.now().minusDays(90), LocalDate.now().minusDays(1));

        productService.processProduct(product);

        assertEquals(0, product.getAvailable());
        verify(notificationService).sendOutOfStockNotification("Grapes");
        verify(productRepository).save(product);
    }

    // -- EXPIRABLE products --

    @Test
    public void expirableProduct_inStockAndNotExpired_decrementsAndSaves() {
        Product product = expirableProduct("Butter", 30, LocalDate.now().plusDays(26));

        productService.processProduct(product);

        assertEquals(29, product.getAvailable());
        verify(productRepository).save(product);
        verifyNoInteractions(notificationService);
    }

    @Test
    public void expirableProduct_expired_notifiesAndSetsStockToZero() {
        Product product = expirableProduct("Milk", 6, LocalDate.now().minusDays(2));

        productService.processProduct(product);

        assertEquals(0, product.getAvailable());
        verify(notificationService).sendExpirationNotification("Milk", product.getExpiryDate());
        verify(productRepository).save(product);
    }

    @Test
    public void expirableProduct_outOfStock_notifiesExpiration() {
        Product product = expirableProduct("Yogurt", 0, LocalDate.now().plusDays(10));

        productService.processProduct(product);

        assertEquals(0, product.getAvailable());
        verify(notificationService).sendExpirationNotification("Yogurt", product.getExpiryDate());
        verify(productRepository).save(product);
    }

    // -- Helper factory methods --

    private Product normalProduct(String name, int available, int leadTime) {
        return new Product(null, leadTime, available, ProductType.NORMAL, name, null, null, null);
    }

    private Product seasonalProduct(String name, int available, int leadTime,
                                    LocalDate seasonStart, LocalDate seasonEnd) {
        return new Product(null, leadTime, available, ProductType.SEASONAL, name, null, seasonStart, seasonEnd);
    }

    private Product expirableProduct(String name, int available, LocalDate expiryDate) {
        return new Product(null, 0, available, ProductType.EXPIRABLE, name, expiryDate, null, null);
    }
}