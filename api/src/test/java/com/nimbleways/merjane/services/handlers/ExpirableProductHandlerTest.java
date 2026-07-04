package com.nimbleways.merjane.services.handlers;

import com.nimbleways.merjane.persistence.entities.Product;
import com.nimbleways.merjane.domain.ProductType;
import com.nimbleways.merjane.persistence.repositories.ProductRepository;
import com.nimbleways.merjane.services.NotificationService;
import com.nimbleways.merjane.utils.Annotations.UnitTest;
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
public class ExpirableProductHandlerTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ExpirableProductHandler handler;

    @Test
    public void availableGreaterThanZero_notExpired_decrementsAndSaves() {
        Product product = new Product(null, 15, 10, ProductType.EXPIRABLE, "Butter",
                LocalDate.now().plusDays(26), null, null);
        Mockito.when(productRepository.save(product)).thenReturn(product);

        handler.handle(product);

        assertEquals(9, product.getAvailable());
        Mockito.verify(productRepository).save(product);
        Mockito.verifyNoInteractions(notificationService);
    }

    @Test
    public void expired_setsAvailableToZeroAndNotifiesExpiration() {
        Product product = new Product(null, 90, 6, ProductType.EXPIRABLE, "Milk",
                LocalDate.now().minusDays(2), null, null);
        Mockito.when(productRepository.save(product)).thenReturn(product);

        handler.handle(product);

        assertEquals(0, product.getAvailable());
        Mockito.verify(productRepository).save(product);
        Mockito.verify(notificationService).sendExpirationNotification("Milk", product.getExpiryDate());
    }

    @Test
    public void availableZero_notExpired_setsAvailableToZeroAndNotifiesExpiration() {
        Product product = new Product(null, 15, 0, ProductType.EXPIRABLE, "Cheese",
                LocalDate.now().plusDays(30), null, null);
        Mockito.when(productRepository.save(product)).thenReturn(product);

        handler.handle(product);

        assertEquals(0, product.getAvailable());
        Mockito.verify(productRepository).save(product);
        Mockito.verify(notificationService).sendExpirationNotification("Cheese", product.getExpiryDate());
    }
}
