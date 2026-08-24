package com.nimbleways.springboilerplate.services;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.ProductType;
import com.nimbleways.springboilerplate.exceptions.BusinessException;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import com.nimbleways.springboilerplate.services.implementations.ProductServiceImpl;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

//I chnaged the name for MyUnitTest to ProductServiceUnitTest, because I think is more descriptive and specific
// also the notification testing should be separated for better understanding of the code
@ExtendWith(SpringExtension.class)
@UnitTest
public class ProductServiceTests {

    @Mock
    private NotificationService notificationService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    // for this case, I think is gonna be better to let the
    // notifgication service handle it, first I made notifyDelay private on the product service
    // but I saw its usage on this test, so I taught it might be specified by someone else
    @Test
    public void test() {
        // GIVEN
        Product product =new Product(null, 15, 0, ProductType.NORMAL, "RJ45 Cable", null, null, null);

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
    void fulfillsNormalProductWhenAvailable() {
        var product = product(ProductType.NORMAL, 2, 0, null, null, null);

        var response = process(product);

        assertEquals(ORDER_ID, response.id());
        assertEquals(1, product.getAvailable());
        verify(productRepository).save(product);
        verifyNoInteractions(notificationService);
    }

    @Test
    void notifiesDelayForUnavailableNormalProductWithLeadTime() {
        Product product = product(ProductType.NORMAL, 0, 3, null, null, null);

        process(product);

        verify(productRepository).save(product);
        verify(notificationService).sendDelayNotification(3, product.getName());
    }

    @Test
    void leavesUnavailableNormalProductWithoutLeadTimeUnchanged() {
        Product product = product(ProductType.NORMAL, 0, 0, null, null, null);

        process(product);

        verifyNoInteractions(productRepository, notificationService);
    }

    @Test
    void fulfillsAvailableSeasonalProductDuringItsSeason() {
        Product product = product(ProductType.SEASONAL, 2, 1, null, LocalDate.now().minusDays(1), LocalDate.now().plusDays(2));

        process(product);

        assertEquals(1, product.getAvailable());
        verify(productRepository).save(product);
        verifyNoInteractions(notificationService);
    }

    @Test
    void marksSeasonalProductOutOfStockWhenLeadTimeExceedsSeasonEnd() {
        Product product = product(ProductType.SEASONAL, 0, 2, null, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));

        process(product);

        assertEquals(0, product.getAvailable());
        verify(productRepository).save(product);
        verify(notificationService).sendOutOfStockNotification(product.getName());
    }

    @Test
    void notifiesOutOfStockForSeasonalProductBeforeItsSeason() {
        Product product = product(ProductType.SEASONAL, 2, 1, null, LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));

        process(product);

        assertEquals(2, product.getAvailable());
        verify(productRepository).save(product);
        verify(notificationService).sendOutOfStockNotification(product.getName());
    }

    @Test
    void notifiesDelayForUnavailableSeasonalProductWithinItsSeason() {
        Product product = product(ProductType.SEASONAL, 0, 1, null, LocalDate.now().minusDays(1), LocalDate.now().plusDays(3));

        process(product);

        verify(productRepository).save(product);
        verify(notificationService).sendDelayNotification(1, product.getName());
    }

    @Test
    void fulfillsAvailableUnexpiredProduct() {
        Product product = product(ProductType.EXPIRABLE, 2, 0, LocalDate.now().plusDays(1), null, null);

        process(product);

        assertEquals(1, product.getAvailable());
        verify(productRepository).save(product);
        verifyNoInteractions(notificationService);
    }

    @Test
    void notifiesExpirationForExpiredProduct() {
        Product product = product(ProductType.EXPIRABLE, 2, 0, LocalDate.now().minusDays(1), null, null);

        process(product);

        assertEquals(0, product.getAvailable());
        verify(productRepository).save(product);
        verify(notificationService).sendExpirationNotification(product.getName(), product.getExpiryDate());
    }

    @Test
    void throwsBusinessExceptionWhenOrderDoesNotExist() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> productService.processOrder(ORDER_ID));

        assertEquals("error.order.not.found", exception.getMessageCode());
        verifyNoInteractions(productRepository, notificationService);
    }

    private ProcessOrderResponse process(Product product) {
        var order = new Order(ORDER_ID, Set.of(product));
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        return productService.processOrder(ORDER_ID);
    }

    private Product product(
            ProductType productType,
            int available,
            int leadTime,
            LocalDate expiryDate,
            LocalDate seasonStartDate,
            LocalDate seasonEndDate) {
        return new Product(null, leadTime, available, productType, "Product name", expiryDate, seasonStartDate, seasonEndDate);
    }

    private static final Long ORDER_ID = 1L;
}