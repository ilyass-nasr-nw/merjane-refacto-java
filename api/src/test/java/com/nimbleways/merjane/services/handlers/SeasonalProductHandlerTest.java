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
public class SeasonalProductHandlerTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private SeasonalProductHandler handler;

    @Test
    public void inSeason_availableGreaterThanZero_decrementsAndSaves() {
        Product product = new Product(null, 15, 10, ProductType.SEASONAL, "Watermelon", null,
                LocalDate.now().minusDays(2), LocalDate.now().plusDays(58));
        Mockito.when(productRepository.save(product)).thenReturn(product);

        handler.handle(product);

        assertEquals(9, product.getAvailable());
        Mockito.verify(productRepository).save(product);
        Mockito.verifyNoInteractions(notificationService);
    }

    @Test
    public void deliveryExceedsSeasonEnd_setsAvailableToZeroAndNotifiesOutOfStock() {
        Product product = new Product(null, 1, 5, ProductType.SEASONAL, "Strawberry", null,
                LocalDate.now().minusDays(100), LocalDate.now().minusDays(10));
        Mockito.when(productRepository.save(product)).thenReturn(product);

        handler.handle(product);

        assertEquals(0, product.getAvailable());
        Mockito.verify(productRepository).save(product);
        Mockito.verify(notificationService).sendOutOfStockNotification("Strawberry");
    }

    @Test
    public void seasonNotStarted_notifiesOutOfStock() {
        Product product = new Product(null, 15, 30, ProductType.SEASONAL, "Grapes", null,
                LocalDate.now().plusDays(180), LocalDate.now().plusDays(240));
        Mockito.when(productRepository.save(product)).thenReturn(product);

        handler.handle(product);

        assertEquals(30, product.getAvailable());
        Mockito.verify(productRepository).save(product);
        Mockito.verify(notificationService).sendOutOfStockNotification("Grapes");
    }

    @Test
    public void inSeason_availableZero_sendsDelayNotification() {
        Product product = new Product(null, 5, 0, ProductType.SEASONAL, "Mango", null,
                LocalDate.now().minusDays(2), LocalDate.now().plusDays(58));
        Mockito.when(productRepository.save(product)).thenReturn(product);

        handler.handle(product);

        assertEquals(0, product.getAvailable());
        Mockito.verify(productRepository).save(product);
        Mockito.verify(notificationService).sendDelayNotification(5, "Mango");
    }
}
