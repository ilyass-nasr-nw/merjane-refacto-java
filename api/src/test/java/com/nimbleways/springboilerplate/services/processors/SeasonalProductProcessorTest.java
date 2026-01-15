package com.nimbleways.springboilerplate.services.processors;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link SeasonalProductProcessor}.
 *
 * <p><b>Objective:</b> Validate the SEASONAL product rules described in the cahier des charges:</p>
 * <ul>
 *   <li>SEASONAL products can only be sold within their season window.</li>
 *   <li>If out of stock, a delay can be announced to the customer.</li>
 *   <li>If the restock delay exceeds the season end date, the product becomes unavailable
 *       and an "out of stock" notification must be sent.</li>
 * </ul>
 *
 * <p><b>Why using a fixed Clock?</b></p>
 * <p>
 * The business logic depends on {@code LocalDate.now()}. In order to make the tests deterministic
 * and stable across different machines/timezones/days, we inject a fixed {@link Clock}.
 * </p>
 */
class SeasonalProductProcessorTest {

    private final ProductRepository productRepository = Mockito.mock(ProductRepository.class);

    private final NotificationService notificationService = Mockito.mock(NotificationService.class);


    private final Clock fixedClock = Clock.fixed(
            Instant.parse("2026-01-15T00:00:00Z"),
            ZoneId.of("UTC")
    );


    private final SeasonalProductProcessor processor =
            new SeasonalProductProcessor(productRepository, notificationService, fixedClock);

    /**
     * Scenario: SEASONAL product, currently within the season and has stock.
     *
     * <p><b>Expected behavior:</b></p>
     * <ul>
     *   <li>available is decremented by 1</li>
     *   <li>product is saved</li>
     *   <li>no notification is sent</li>
     * </ul>
     */
    @Test
    void process_shouldDecrementStock_whenInSeason_andHasStock() {
        Product product = new Product(
                1L, 10, 5, "SEASONAL", "Watermelon",
                null,
                LocalDate.of(2026, 1, 10),
                LocalDate.of(2026, 2, 10)
        );

        processor.process(product);

        assertEquals(4, product.getAvailable());
        Mockito.verify(productRepository).save(product);
        Mockito.verifyNoInteractions(notificationService);
    }

    /**
     * Scenario: SEASONAL product out of stock and restock delay would exceed season end.
     *
     * <p><b>Expected behavior:</b></p>
     * <ul>
     *   <li>product is considered unavailable</li>
     *   <li>available must be set to 0</li>
     *   <li>out-of-stock notification must be sent</li>
     *   <li>product must be saved</li>
     * </ul>
     */
    @Test
    void process_shouldNotifyOutOfStock_andSetStockToZero_whenRestockExceedsSeasonEnd() {
        Product product = new Product(
                1L, 30, 0, "SEASONAL", "Strawberry",
                null,
                LocalDate.of(2026, 1, 10),
                LocalDate.of(2026, 1, 20)
        );

        processor.process(product);

        assertEquals(0, product.getAvailable());
        Mockito.verify(notificationService).sendOutOfStockNotification("Strawberry");
        Mockito.verify(productRepository).save(product);
    }

    /**
     * Scenario: SEASONAL product but the season has not started yet.
     *
     * <p><b>Expected behavior:</b></p>
     * <ul>
     *   <li>customer is notified that the product is not available</li>
     *   <li>product is saved</li>
     * </ul>
     */
    @Test
    void process_shouldNotifyOutOfStock_whenSeasonNotStartedYet() {
        Product product = new Product(
                1L, 10, 0, "SEASONAL", "Grapes",
                null,
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 3, 1)
        );

        processor.process(product);

        Mockito.verify(notificationService).sendOutOfStockNotification("Grapes");
        Mockito.verify(productRepository).save(product);
    }

    /**
     * Scenario: SEASONAL product out of stock, but restock is possible before season end.
     *
     * <p><b>Expected behavior:</b></p>
     * <ul>
     *   <li>a delay notification is sent</li>
     *   <li>product is saved</li>
     * </ul>
     */
    @Test
    void process_shouldNotifyDelay_whenRestockIsPossibleWithinSeason() {
        Product product = new Product(
                1L, 2, 0, "SEASONAL", "Peach",
                null,
                LocalDate.of(2026, 1, 10),
                LocalDate.of(2026, 1, 25)
        );

        processor.process(product);

        Mockito.verify(notificationService).sendDelayNotification(2, "Peach");
        Mockito.verify(productRepository).save(product);
    }
}
