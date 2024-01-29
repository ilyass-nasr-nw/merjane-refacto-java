package com.nimbleways.springboilerplate.services;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.impl.FlashSaleProductServiceImpl;
import com.nimbleways.springboilerplate.services.impl.NotificationService;
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
public class FlashSaleProductUnitTests {

    @Mock
    private NotificationService notificationService;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks 
    private FlashSaleProductServiceImpl productService;

    @Test
    public void test() {
        // GIVEN
        Product product = new Product(null, 15, 10, "FLASHSALE", "Cheminee", LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(5), 100);

        Mockito.when(productRepository.save(product)).thenReturn(product);

        // WHEN
        productService.handle(product);

        // THEN
        assertEquals(9, product.getAvailable());
        assertEquals(15, product.getLeadTime());
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
        // Mockito.verify(notificationService, Mockito.times(1)).sendDelayNotification(product.getLeadTime(), product.getName());
    }
}