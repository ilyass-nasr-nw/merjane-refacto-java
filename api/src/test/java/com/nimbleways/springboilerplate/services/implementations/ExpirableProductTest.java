package com.nimbleways.springboilerplate.services.implementations;
import com.nimbleways.springboilerplate.constants.ProductType;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.handler.ExpiredProduct;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@UnitTest
public class ExpirableProductTest {

    @Mock
    private NotificationService mockNotificationService;

    @Mock
    private ProductRepository mockProductRepository;

    @InjectMocks
    private ExpiredProduct expiredProductHandler;

    private Product expiredMilkProduct;

    private Product freshButterProduct;

    @BeforeEach
    void init() {
        expiredMilkProduct = new Product(1L, 0, 10, ProductType.EXPIRABLE, "Milk", LocalDate.now().minusDays(2), null, null);
        freshButterProduct = new Product(2L, 5, 20, ProductType.EXPIRABLE, "Tomat", LocalDate.now().plusDays(5), null, null);
    }

    @Test
    public void shouldMarkExpiredProductAsUnavailableAndNotify() {
        expiredProductHandler.manage(expiredMilkProduct);

        assertEquals(0, expiredMilkProduct.getAvailable());
        verify(mockNotificationService, times(1)).sendExpirationNotification("Milk", expiredMilkProduct.getExpiryDate());
        verify(mockProductRepository, times(1)).save(expiredMilkProduct);
    }

    @Test
    public void shouldNotNotifyForNonExpiredProduct() {
        expiredProductHandler.manage(freshButterProduct);

        assertEquals(4, freshButterProduct.getAvailable()); 
        verify(mockNotificationService, times(0)).sendExpirationNotification(freshButterProduct.getName(), freshButterProduct.getExpiryDate());
        verify(mockProductRepository, times(1)).save(freshButterProduct);
    }
}