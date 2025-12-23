package com.nimbleways.springboilerplate.services.implementations;
import com.nimbleways.springboilerplate.constants.ProductType;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.handler.NormalProduct;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@UnitTest
public class MyUnitTests {

    @Mock
    private NormalProduct normalProductHandler;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService; //  Get handlers list

    private Product inStockProduct;
    private Product outOfStockProduct;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        inStockProduct = new Product(null, 10, 5, ProductType.NORMAL, "air fryer", null, null, null);
        outOfStockProduct = new Product(null, 10, 0, ProductType.NORMAL, "LG TV", null, null, null);
    }

    @Test
    public void shouldProcessInStockNormalProduct() {
        doAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setAvailable(p.getAvailable() - 1);
            return null;
        }).when(normalProductHandler).manage(inStockProduct);

                productService.handleProduct(inStockProduct);

        assertEquals(4, inStockProduct.getAvailable());
        verify(normalProductHandler, times(1)).manage(inStockProduct);
        verifyNoInteractions(notificationService);
    }

    @Test
    public void shouldNotifyAndProcessOutOfStockProduct() {
        doAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setAvailable(0);
            notificationService.sendDelayNotification(p.getLeadTime(), p.getName());
            return null;
        }).when(normalProductHandler).manage(outOfStockProduct);

        productService.handleProduct(outOfStockProduct);

        assertEquals(0, outOfStockProduct.getAvailable());
        verify(normalProductHandler, times(1)).manage(outOfStockProduct);
        verify(notificationService, times(1)).sendDelayNotification(outOfStockProduct.getLeadTime(), outOfStockProduct.getName());
    }
}