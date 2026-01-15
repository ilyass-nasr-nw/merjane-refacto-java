package com.nimbleways.springboilerplate.services.processors;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.ProductType;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.ProductProcessor;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Processor responsible for applying inventory rules to SEASONAL products.
 *
 * <p>Rules summary:
 * <ul>
 *   <li>If in season and in stock => decrement available and save.</li>
 *   <li>If restock exceeds season end => notify out-of-stock, set available=0, save.</li>
 *   <li>If season not started yet => notify out-of-stock, save.</li>
 *   <li>Otherwise => notify delay (restock possible), save leadTime.</li>
 * </ul>
 * </p>
 */
@Slf4j
@Component
public class SeasonalProductProcessor implements ProductProcessor {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;
    private final Clock clock;

    /**
     * Creates a {@link SeasonalProductProcessor}.
     *
     * @param productRepository repository used to persist product updates
     * @param notificationService service used to send customer notifications
     * @param clock clock used for date computations (test-friendly)
     */
    public SeasonalProductProcessor(ProductRepository productRepository,
                                    NotificationService notificationService,
                                    Clock clock) {
        this.productRepository = Objects.requireNonNull(productRepository, "productRepository must not be null");
        this.notificationService = Objects.requireNonNull(notificationService, "notificationService must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    /**
     * Checks whether this processor supports the given product.
     *
     * @param product product to check
     * @return true if product type is SEASONAL, false otherwise
     */
    @Override
    public boolean supports(Product product) {
        return product != null && product.getProductType() == ProductType.SEASONAL;
    }

    /**
     * Applies seasonal rules on the product.
     *
     * @param product product to process
     * @throws IllegalArgumentException if product is null
     */
    @Override
    public void process(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("product must not be null");
        }

        LocalDate today = LocalDate.now(clock);
        int leadTime = safeLeadTime(product);

        log.debug("Processing seasonal product. productId={} name={} available={}",
                product.getId(),
                product.getName(),
                product.getAvailable()
        );

        if (canSellNow(product, today)) {
            decrementStock(product);
            productRepository.save(product);
            return;
        }

        if (restockWouldExceedSeasonEnd(product, today, leadTime)) {
            log.info("Seasonal product unavailable (restock after season end). productId={} name={}",
                    product.getId(),
                    product.getName()
            );

            notificationService.sendOutOfStockNotification(product.getName());
            markOutOfStock(product);
            productRepository.save(product);
            return;
        }

        if (seasonNotStartedYet(product, today)) {
            log.info("Seasonal product unavailable (season not started yet). productId={} name={}",
                    product.getId(),
                    product.getName()
            );

            notificationService.sendOutOfStockNotification(product.getName());
            productRepository.save(product);
            return;
        }

        log.info("Seasonal product delayed. productId={} name={} leadTime={}",
                product.getId(),
                product.getName(),
                leadTime
        );

        product.setLeadTime(leadTime);
        productRepository.save(product);
        notificationService.sendDelayNotification(leadTime, product.getName());
    }

    /**
     * Determines if a seasonal product can be sold at the given date.
     *
     * @param product the product being checked
     * @param today current date
     * @return true if the product is in season and has stock, false otherwise
     */
    private boolean canSellNow(Product product, LocalDate today) {
        return hasStock(product) && isInSeason(product, today);
    }

    /**
     * Checks whether the product has at least one unit available.
     *
     * @param product the product being checked
     * @return true if available is not null and greater than 0, false otherwise
     */
    private boolean hasStock(Product product) {
        return product.getAvailable() != null && product.getAvailable() > 0;
    }

    /**
     * Checks whether the current date is within the product's season window.
     *
     * <p>
     * Note: this uses strict boundaries to keep the same behavior as the initial code:
     * {@code today.isAfter(start)} AND {@code today.isBefore(end)}.
     * </p>
     *
     * @param product the product being checked
     * @param today current date
     * @return true if today is strictly within (startDate, endDate), false otherwise
     */
    private boolean isInSeason(Product product, LocalDate today) {
        return today.isAfter(product.getSeasonStartDate())
                && today.isBefore(product.getSeasonEndDate());
    }

    /**
     * Checks whether restocking the product would exceed the season end date.
     *
     * <p>
     * If {@code today + leadTime > seasonEndDate}, then the product is considered unavailable.
     * </p>
     *
     * @param product the product being checked
     * @param today current date
     * @param leadTime restocking delay in days (already normalized)
     * @return true if restock date exceeds season end date, false otherwise
     */
    private boolean restockWouldExceedSeasonEnd(Product product, LocalDate today, int leadTime) {
        return today.plusDays(leadTime).isAfter(product.getSeasonEndDate());
    }

    /**
     * Checks if the season is not yet started at the current date.
     *
     * @param product the product being checked
     * @param today current date
     * @return true if season start date is after today, false otherwise
     */
    private boolean seasonNotStartedYet(Product product, LocalDate today) {
        return product.getSeasonStartDate().isAfter(today);
    }

    /**
     * Normalizes the lead time value to avoid null handling in business logic.
     *
     * @param product the product being checked
     * @return lead time if defined, otherwise 0
     */
    private int safeLeadTime(Product product) {
        return product.getLeadTime() != null ? product.getLeadTime() : 0;
    }

    /**
     * Decrements the available stock by one unit.
     *
     * @param product product to update
     */
    private void decrementStock(Product product) {
        product.setAvailable(product.getAvailable() - 1);
    }

    /**
     * Marks the product as unavailable by setting its available stock to 0.
     *
     * @param product product to update
     */
    private void markOutOfStock(Product product) {
        product.setAvailable(0);
    }
}
