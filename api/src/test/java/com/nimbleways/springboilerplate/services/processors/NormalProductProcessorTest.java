package com.nimbleways.springboilerplate.services.processors;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link NormalProductProcessor}.
 *
 * <p>Business rules for NORMAL products (cahier de charge):</p>
 * <ul>
 *   <li>If the product has stock (available > 0): decrement stock by 1.</li>
 *   <li>If the product is out of stock (available <= 0) and leadTime > 0: announce a delay to customers.</li>
 *   <li>If the product is out of stock and leadTime <= 0: do nothing.</li>
 * </ul>
 */
class NormalProductProcessorTest {

    private final ProductRepository productRepository = Mockito.mock(ProductRepository.class);
    private final NotificationService notificationService = Mockito.mock(NotificationService.class);

    private final NormalProductProcessor processor =
            new NormalProductProcessor(productRepository, notificationService);

    /**
     * Case: NORMAL product is in stock.
     *
     * Expected behavior:
     * <ul>
     *   <li>available is decremented by 1</li>
     *   <li>product is saved</li>
     *   <li>no notification is sent</li>
     * </ul>
     */
    @Test
    void process_shouldDecrementStock_whenAvailableIsGreaterThanZero() {
        Product product = new Product(1L, 15, 5, "NORMAL", "USB Cable", null, null, null);

        processor.process(product);

        assertEquals(4, product.getAvailable());
        Mockito.verify(productRepository).save(product);
        Mockito.verifyNoInteractions(notificationService);
    }

    /**
     * Case: NORMAL product is out of stock and leadTime is positive.
     *
     * Expected behavior:
     * <ul>
     *   <li>a delay notification is sent to the customer</li>
     *   <li>product is saved</li>
     * </ul>
     */
    @Test
    void process_shouldNotifyDelay_whenOutOfStock_andLeadTimeGreaterThanZero() {
        Product product = new Product(1L, 10, 0, "NORMAL", "USB Dongle", null, null, null);

        processor.process(product);

        Mockito.verify(productRepository).save(product);
        Mockito.verify(notificationService).sendDelayNotification(10, "USB Dongle");
    }

    /**
     * Case: NORMAL product is out of stock and leadTime is zero.
     *
     * Expected behavior:
     * <ul>
     *   <li>no save</li>
     *   <li>no notification</li>
     * </ul>
     */
    @Test
    void process_shouldDoNothing_whenOutOfStock_andLeadTimeIsZero() {
        Product product = new Product(1L, 0, 0, "NORMAL", "Keyboard", null, null, null);

        processor.process(product);

        Mockito.verifyNoInteractions(productRepository);
        Mockito.verifyNoInteractions(notificationService);
    }
}
