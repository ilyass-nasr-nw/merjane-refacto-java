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

    @Test
    public void notifyDelayTest() {
        // GIVEN
        Product product =new Product(null, 15, 0, "NORMAL", "RJ45 Cable", null, null, null);

        Mockito.when(productRepository.save(product)).thenReturn(product);

        // WHEN
        productService.notifyDelay(product.getLeadTime(), product);

        // THEN
        assertEquals(0, product.getAvailable());
        assertEquals(15, product.getLeadTime());
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
        Mockito.verify(notificationService, Mockito.times(1)).sendDelayNotification(product.getLeadTime(), product.getName());
    }


    @Test
    public void normalProductWithStock() {
        Product product = new Product(null, 15, 5, "NORMAL", "RJ45 Cable", null, null, null);
        Mockito.when(productRepository.save(product)).thenReturn(product);

        productService.handleNormalProduct(product);

        assertEquals(4, product.getAvailable());
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
        Mockito.verifyNoInteractions(notificationService);
    }

    @Test
    public void normalProductOutOfStock() {
        Product product = new Product(null, 15, 0, "NORMAL", "RJ45 Cable", null, null, null);
        Mockito.when(productRepository.save(product)).thenReturn(product);

        productService.handleNormalProduct(product);

        assertEquals(0, product.getAvailable());
        assertEquals(15, product.getLeadTime());
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
        Mockito.verify(notificationService, Mockito.times(1)).sendDelayNotification(15, "RJ45 Cable");
    }


    @Test
    public void productWithStockNotExpired() {
        Product product = new Product(null, 15, 10, "EXPIRABLE", "Butter", LocalDate.now().plusDays(10), null, null);
        Mockito.when(productRepository.save(product)).thenReturn(product);

        productService.handleExpirableProduct(product);

        assertEquals(9, product.getAvailable());
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
        Mockito.verifyNoInteractions(notificationService);
    }

    @Test
    public void productExpired() {
        LocalDate expiryDate = LocalDate.now().minusDays(1);
        Product product = new Product(null, 15, 6, "EXPIRABLE", "Milk", expiryDate, null, null);
        Mockito.when(productRepository.save(product)).thenReturn(product);

        productService.handleExpirableProduct(product);

        assertEquals(0, product.getAvailable());
        Mockito.verify(notificationService, Mockito.times(1)).sendExpirationNotification("Milk", expiryDate);
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
    }

    @Test
    public void productNoStock() {
        LocalDate expiryDate = LocalDate.now().plusDays(5);
        Product product = new Product(null, 15, 0, "EXPIRABLE", "Cheese", expiryDate, null, null);
        Mockito.when(productRepository.save(product)).thenReturn(product);

        productService.handleExpirableProduct(product);

        assertEquals(0, product.getAvailable());
        Mockito.verify(notificationService, Mockito.times(1)).sendExpirationNotification("Cheese", expiryDate);
    }


    @Test
    public void seasonalProductInSeason() {
        Product product = new Product(null, 15, 10, "SEASONAL", "Watermelon", null,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(30));
        Mockito.when(productRepository.save(product)).thenReturn(product);

        productService.handleSeasonalProduct(product);

        assertEquals(9, product.getAvailable());
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
        Mockito.verifyNoInteractions(notificationService);
    }
}