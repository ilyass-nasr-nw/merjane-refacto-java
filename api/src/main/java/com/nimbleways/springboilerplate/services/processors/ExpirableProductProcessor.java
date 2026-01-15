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
 * Processor responsible for applying inventory rules to EXPIRABLE products.
 *
 * <p>Business rules:
 * <ul>
 *   <li>If the product has stock and is not expired: decrement stock by 1 and save.</li>
 *   <li>Otherwise: notify expiration, set stock to 0 and save.</li>
 * </ul>
 * </p>
 *
 * <p>
 * The {@link Clock} dependency makes date checks deterministic and easily testable.
 * </p>
 */
@Slf4j
@Component
public class ExpirableProductProcessor implements ProductProcessor {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;
    private final Clock clock;

    /**
     * Creates an {@link ExpirableProductProcessor}.
     *
     * @param productRepository repository used to persist product updates
     * @param notificationService service used to notify customers
     * @param clock clock used for date/time computations
     */
    public ExpirableProductProcessor(ProductRepository productRepository,
                                     NotificationService notificationService,
                                     Clock clock) {
        this.productRepository = Objects.requireNonNull(productRepository, "productRepository must not be null");
        this.notificationService = Objects.requireNonNull(notificationService, "notificationService must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    /**
     * Checks if the processor supports the given product.
     *
     * @param product the product to check
     * @return true if product type is EXPIRABLE
     */
    @Override
    public boolean supports(Product product) {
        return product != null && product.getProductType() == ProductType.EXPIRABLE;
    }

    /**
     * Applies expirable rules to the product.
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

        boolean hasStock = product.getAvailable() != null && product.getAvailable() > 0;
        boolean notExpired = product.getExpiryDate() != null && product.getExpiryDate().isAfter(today);

        if (hasStock && notExpired) {
            product.setAvailable(product.getAvailable() - 1);
            productRepository.save(product);

            log.info("Expirable product sold successfully. productId={} name={} newAvailable={}",
                    product.getId(),
                    product.getName(),
                    product.getAvailable()
            );

            return;
        }

        log.info("Expirable product is not sellable. Marking out of stock and notifying expiration. productId={} name={} expiryDate={}",
                product.getId(),
                product.getName(),
                product.getExpiryDate()
        );

        notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());
        product.setAvailable(0);
        productRepository.save(product);
    }
}
