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

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@UnitTest
public class NormalProductHandlerTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NormalProductHandler handler;

    @Test
    public void available_greaterThanZero_decrementsAndSaves() {
        Product product = new Product(null, 15, 10, ProductType.NORMAL, "USB Cable", null, null, null);
        Mockito.when(productRepository.save(product)).thenReturn(product);

        handler.handle(product);

        assertEquals(9, product.getAvailable());
        Mockito.verify(productRepository).save(product);
        Mockito.verifyNoInteractions(notificationService);
    }

    @Test
    public void available_zero_leadTimeGreaterThanZero_sendsDelayNotification() {
        Product product = new Product(null, 10, 0, ProductType.NORMAL, "USB Dongle", null, null, null);
        Mockito.when(productRepository.save(product)).thenReturn(product);

        handler.handle(product);

        assertEquals(0, product.getAvailable());
        Mockito.verify(productRepository).save(product);
        Mockito.verify(notificationService).sendDelayNotification(10, "USB Dongle");
    }

    @Test
    public void available_zero_leadTimeZero_doesNothing() {
        Product product = new Product(null, 0, 0, ProductType.NORMAL, "RJ45 Cable", null, null, null);

        handler.handle(product);

        assertEquals(0, product.getAvailable());
        Mockito.verifyNoInteractions(productRepository);
        Mockito.verifyNoInteractions(notificationService);
    }
}
